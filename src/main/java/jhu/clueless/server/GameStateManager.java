package jhu.clueless.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Game State Management component (SRS 2.3.2).
 * Skeletal stub: tracks token locations and the broadcast sequence number; hands and the
 * case file are hard-coded until the Minimal increment.
 */
@Component
public class GameStateManager {

    private static final Logger log = LoggerFactory.getLogger("GameStateManager");

    private final Map<String, AtomicLong> seqByGame = new ConcurrentHashMap<>();
    private final Map<String, String> locations = new ConcurrentHashMap<>();

    /** Broadcasts are numbered 1, 2, 3, ... per game (SRS 5.1). */
    public long nextSeq(String gameId) {
        return seqByGame.computeIfAbsent(gameId, g -> new AtomicLong()).incrementAndGet();
    }

    public String applyMove(String character, String to) {
        String from = locations.getOrDefault(character, "START");
        locations.put(character, to);
        log.info("applyMove({}, {} -> {})", character, from, to);
        return from;
    }

    public List<String> hand(String playerId) {
        log.info("hand({}) -> stub cards", playerId);
        return List.of("PLUM", "ROPE", "KITCHEN");
    }

    /** Stub case file so an accusation result can be demonstrated. */
    public Map<String, String> caseFile() {
        return Map.of("suspect", "SCARLET", "weapon", "KNIFE", "room", "HALL");
    }
}
