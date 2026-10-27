package jhu.clueless.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Game Logic component (SRS 2.3.2).
 * Skeletal stub: every action is accepted. Rule validation is added in the Minimal increment.
 */
@Component
public class GameLogic {

    private static final Logger log = LoggerFactory.getLogger("GameLogic");

    public boolean validateAction(String playerId, String type) {
        log.info("validateAction({}, {}) -> true (stub)", playerId, type);
        return true;
    }
}
