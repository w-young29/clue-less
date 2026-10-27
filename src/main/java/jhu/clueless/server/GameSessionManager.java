package jhu.clueless.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Game Session Management component (SRS 2.3.2).
 * Skeletal stub: keeps just enough (games, players, tokens) to address messages.
 * Characters are handed out in join order until character selection is built.
 */
@Component
public class GameSessionManager {

    private static final Logger log = LoggerFactory.getLogger("GameSessionManager");
    private static final List<String> CHARACTERS = List.of("SCARLET", "MUSTARD", "WHITE", "GREEN", "PEACOCK", "PLUM");

    private final Map<String, List<String>> playersByGame = new ConcurrentHashMap<>();
    private final Map<String, String> playerByToken = new ConcurrentHashMap<>();
    private final Map<String, String> characters = new ConcurrentHashMap<>();

    public String createGame() {
        String gameId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        playersByGame.put(gameId, new CopyOnWriteArrayList<>());
        log.info("createGame() -> {}", gameId);
        return gameId;
    }

    /** Returns {playerId, token}. */
    public String[] addPlayer(String gameId, String username) {
        List<String> players = playersByGame.get(gameId);
        String playerId = "P-" + UUID.randomUUID().toString().substring(0, 4);
        String token = UUID.randomUUID().toString();
        characters.put(playerId, CHARACTERS.get(players.size() % CHARACTERS.size()));
        players.add(playerId);
        playerByToken.put(token, playerId);
        log.info("addPlayer({}, {}) -> {} as {}", gameId, username, playerId, characters.get(playerId));
        return new String[] {playerId, token};
    }

    public boolean gameExists(String gameId) { return playersByGame.containsKey(gameId); }
    public String playerForToken(String token) { return token == null ? null : playerByToken.get(token); }
    public List<String> players(String gameId) { return playersByGame.getOrDefault(gameId, List.of()); }
    public String character(String playerId) { return characters.get(playerId); }
}
