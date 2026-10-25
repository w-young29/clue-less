package jhu.clueless.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import jhu.clueless.messaging.MessageEnvelope;
import jhu.clueless.messaging.MessagePayloads.PlayerMovedPayload;


@RestController
public class ClueLessController {

  @GetMapping("/")
  public String test() {
    return "Service running successfully!";
  }

 @GetMapping("/demo/message")
    public MessageEnvelope demoMessage() {
        PlayerMovedPayload payload = new PlayerMovedPayload(
            "SCARLET",
            "HALL",
            "BILLIARD_ROOM",
            "HALLWAY"
        );

        return new MessageEnvelope(
            "B6_PLAYER_MOVED",
            "demo-game-123",
            1L,
            Map.of(
                "character", payload.character(),
                "from", payload.from(),
                "to", payload.to(),
                "via", payload.via()
            )
        );
    }
}
