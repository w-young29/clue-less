package jhu.clueless.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Suggestion and Accusation component (SRS 2.3.2).
 * Skeletal stub: compares an accusation with the stub case file. Suggestions and the
 * disprove cycle are added in the Minimal increment.
 */
@Component
public class SuggestionAccusationManager {

    private static final Logger log = LoggerFactory.getLogger("SuggestionAccusation");

    private final GameStateManager state;

    public SuggestionAccusationManager(GameStateManager state) {
        this.state = state;
    }

    public boolean checkAccusation(String suspect, String weapon, String room) {
        Map<String, String> caseFile = state.caseFile();
        boolean correct = caseFile.get("suspect").equals(suspect) && caseFile.get("weapon").equals(weapon)
            && caseFile.get("room").equals(room);
        log.info("checkAccusation({}, {}, {}) -> {}", suspect, weapon, room, correct);
        return correct;
    }
}
