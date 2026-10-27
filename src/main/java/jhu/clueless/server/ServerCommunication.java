package jhu.clueless.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jhu.clueless.messaging.BroadcastMessageType;
import jhu.clueless.messaging.ClientMessageType;
import jhu.clueless.messaging.MessageEnvelope;
import jhu.clueless.messaging.MessagePayloads.AccusationRequest;
import jhu.clueless.messaging.MessagePayloads.MoveRequest;
import jhu.clueless.messaging.MessagePayloads.PlayerMovedPayload;
import jhu.clueless.messaging.UnicastMessageType;
import jhu.clueless.websocket.ClueLessWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Server Communication component (SRS 2.3.2).
 * Receives each client request (C#) from the REST controller, calls the other server
 * components, and sends the resulting messages to clients over WebSocket:
 * unicast (U#) to one player, broadcast (B#) to everyone in the game.
 */
@Component
public class ServerCommunication {

    private static final Logger log = LoggerFactory.getLogger("ServerCommunication");

    private final GameSessionManager sessions;
    private final GameLogic logic;
    private final GameStateManager state;
    private final SuggestionAccusationManager suggestions;
    private final ClueLessWebSocketHandler sockets;
    private final ObjectMapper mapper;

    public ServerCommunication(GameSessionManager sessions, GameLogic logic, GameStateManager state,
                               SuggestionAccusationManager suggestions, ClueLessWebSocketHandler sockets,
                               ObjectMapper mapper) {
        this.sessions = sessions;
        this.logic = logic;
        this.state = state;
        this.suggestions = suggestions;
        this.sockets = sockets;
        this.mapper = mapper;
    }

    // ---------------------------------------------------------------- lobby

    /** C1: returns gameId, playerId, token. */
    public Map<String, Object> createGame(String username) {
        received(ClientMessageType.C1_CREATE_GAME, null, username);
        String gameId = sessions.createGame();
        String[] ids = sessions.addPlayer(gameId, username);
        return Map.of("gameId", gameId, "playerId", ids[0], "token", ids[1]);
    }

    /** C2 -> B1 */
    public Map<String, Object> joinGame(String gameId, String username) {
        received(ClientMessageType.C2_JOIN_GAME, gameId, username);
        String[] ids = sessions.addPlayer(gameId, username);
        broadcast(gameId, BroadcastMessageType.B1_PLAYER_JOINED, Map.of(
            "playerId", ids[0], "username", username, "playerCount", sessions.players(gameId).size()));
        return Map.of("gameId", gameId, "playerId", ids[0], "token", ids[1]);
    }

    /** C4 -> B4, U1 (each player) */
    public void startGame(String gameId, String playerId) {
        received(ClientMessageType.C4_START_GAME, gameId, playerId);
        logic.validateAction(playerId, "C4_START_GAME");
        broadcast(gameId, BroadcastMessageType.B4_GAME_STARTED, Map.of("players", sessions.players(gameId)));
        for (String p : sessions.players(gameId)) {
            sendTo(gameId, p, UnicastMessageType.U1_HAND_DEALT, Map.of("cards", state.hand(p)));
        }
    }

    // ---------------------------------------------------------------- game play

    /** C6 -> B6 */
    public void move(String gameId, String playerId, MoveRequest req) {
        received(ClientMessageType.C6_MOVE, gameId, playerId);
        logic.validateAction(playerId, "C6_MOVE");
        String character = sessions.character(playerId);
        String from = state.applyMove(character, req.to());
        broadcast(gameId, BroadcastMessageType.B6_PLAYER_MOVED, new PlayerMovedPayload(character, from, req.to(), "HALLWAY"));
    }

    /** C9 -> B12, U5 (accuser only) */
    public void makeAccusation(String gameId, String playerId, AccusationRequest req) {
        received(ClientMessageType.C9_MAKE_ACCUSATION, gameId, playerId);
        boolean correct = suggestions.checkAccusation(req.suspect(), req.weapon(), req.room());
        broadcast(gameId, BroadcastMessageType.B12_ACCUSATION_MADE, Map.of(
            "accuser", playerId, "suspect", req.suspect(), "weapon", req.weapon(), "room", req.room()));
        sendTo(gameId, playerId, UnicastMessageType.U5_ACCUSATION_RESULT, Map.of("correct", correct, "solution", state.caseFile()));
    }

    // ---------------------------------------------------------------- outbound

    private void sendTo(String gameId, String playerId, UnicastMessageType type, Object payload) {
        MessageEnvelope env = new MessageEnvelope(type.name(), gameId, null, toMap(payload));
        log.info("[Server -> {}] {} {}", playerId, type, env.payload());
        sockets.sendToPlayer(gameId, playerId, toJson(env));
    }

    private void broadcast(String gameId, BroadcastMessageType type, Object payload) {
        MessageEnvelope env = new MessageEnvelope(type.name(), gameId, state.nextSeq(gameId), toMap(payload));
        log.info("[Server -> ALL] {} seq={} {}", type, env.seq(), env.payload());
        sockets.broadcast(gameId, toJson(env));
    }

    private void received(ClientMessageType type, String gameId, String from) {
        log.info("[{} -> Server] {} game={}", from, type, gameId);
    }

    private Map<String, Object> toMap(Object payload) {
        return mapper.convertValue(payload, new TypeReference<Map<String, Object>>() {});
    }

    private String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
