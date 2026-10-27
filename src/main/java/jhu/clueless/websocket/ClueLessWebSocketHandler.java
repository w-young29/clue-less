
package jhu.clueless.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles WebSocket connections for the Clue-Less game.
 * Clients connect to /ws?gameId=..&playerId=.. after creating or joining a game,
 * and the server uses this handler to push unicast (U#) and broadcast (B#) messages.
 */
@Component
public class ClueLessWebSocketHandler extends TextWebSocketHandler {

    /** gameId -> (playerId -> socket) */
    private final Map<String, Map<String, WebSocketSession>> sessions = new ConcurrentHashMap<>();

    /**
     * Sends a welcome message when a client connects to the server and remembers which
     * game and player the socket belongs to.
     *
     * @param session the newly established WebSocket session
     * @throws IOException if sending the message fails
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        Map<String, String> query = UriComponentsBuilder.fromUri(session.getUri()).build()
            .getQueryParams().toSingleValueMap();
        String gameId = query.get("gameId");
        String playerId = query.get("playerId");
        if (gameId != null && playerId != null) {
            session.getAttributes().put("gameId", gameId);
            session.getAttributes().put("playerId", playerId);
            sessions.computeIfAbsent(gameId, g -> new ConcurrentHashMap<>()).put(playerId, session);
            System.out.println("Client connected: game=" + gameId + " player=" + playerId);
        }
        sendMessage(session, "Connected to Clue-Less server!");
    }

    /**
     * Logs a message when a client disconnects.
     *
     * @param session the WebSocket session that was closed
     * @param status  the reason the connection was closed
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object gameId = session.getAttributes().get("gameId");
        Object playerId = session.getAttributes().get("playerId");
        if (gameId != null && playerId != null) {
            Map<String, WebSocketSession> game = sessions.get(gameId.toString());
            if (game != null) game.remove(playerId.toString());
        }
        System.out.println("Client disconnected");
    }

    /** Unicast: sends a message to one player in a game. */
    public void sendToPlayer(String gameId, String playerId, String message) {
        WebSocketSession s = sessions.getOrDefault(gameId, Map.of()).get(playerId);
        if (s != null && s.isOpen()) {
            try {
                sendMessage(s, message);
            } catch (IOException e) {
                System.out.println("Could not send to " + playerId + ": " + e.getMessage());
            }
        }
    }

    /** Broadcast: sends a message to every connected player in a game. */
    public void broadcast(String gameId, String message) {
        for (String playerId : sessions.getOrDefault(gameId, Map.of()).keySet()) {
            sendToPlayer(gameId, playerId, message);
        }
    }

    /**
     * Sends a text message to a specific connected client.
     *
     * @param session the WebSocket session to send the message to
     * @param message the message to send to the client
     * @throws IOException if sending the message fails
     */
    public synchronized void sendMessage(WebSocketSession session, String message) throws IOException {
        session.sendMessage(new TextMessage(message));
    }
}
