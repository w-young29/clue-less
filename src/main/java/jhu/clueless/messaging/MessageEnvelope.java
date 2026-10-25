
package jhu.clueless.messaging;

import java.util.Map;

public record MessageEnvelope(
    String type,
    String gameId,
    Long seq,
    Map<String, Object> payload
) {}
