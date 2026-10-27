package jhu.clueless.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import jhu.clueless.messaging.MessageEnvelope;
import jhu.clueless.messaging.MessagePayloads.AccusationRequest;
import jhu.clueless.messaging.MessagePayloads.CreateGameRequest;
import jhu.clueless.messaging.MessagePayloads.JoinGameRequest;
import jhu.clueless.messaging.MessagePayloads.MoveRequest;
import jhu.clueless.messaging.MessagePayloads.PlayerMovedPayload;
import jhu.clueless.server.GameSessionManager;
import jhu.clueless.server.ServerCommunication;


@RestController
public class ClueLessController {

  private static final Map<String, Object> OK = Map.of("accepted", true);

  private final ServerCommunication server;
  private final GameSessionManager sessions;

  public ClueLessController(ServerCommunication server, GameSessionManager sessions) {
    this.server = server;
    this.sessions = sessions;
  }

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

  // ---- Client-to-server messages (SRS Table 5-2) demonstrated in the skeletal increment ----

  // C1
  @PostMapping("/api/games")
  public Map<String, Object> createGame(@RequestBody CreateGameRequest body) {
    return server.createGame(body.username());
  }

  // C2
  @PostMapping("/api/games/{gameId}/players")
  public Map<String, Object> joinGame(@PathVariable String gameId, @RequestBody JoinGameRequest body) {
    if (!sessions.gameExists(gameId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GAME_NOT_FOUND");
    return server.joinGame(gameId, body.username());
  }

  // C4
  @PostMapping("/api/games/{gameId}/start")
  public Map<String, Object> startGame(@PathVariable String gameId, @RequestHeader("Authorization") String auth) {
    server.startGame(gameId, player(auth));
    return OK;
  }

  // C6
  @PostMapping("/api/games/{gameId}/move")
  public Map<String, Object> move(@PathVariable String gameId, @RequestHeader("Authorization") String auth,
                                  @RequestBody MoveRequest body) {
    server.move(gameId, player(auth), body);
    return OK;
  }

  // C9
  @PostMapping("/api/games/{gameId}/accusation")
  public Map<String, Object> accuse(@PathVariable String gameId, @RequestHeader("Authorization") String auth,
                                    @RequestBody AccusationRequest body) {
    server.makeAccusation(gameId, player(auth), body);
    return OK;
  }

  /** The player is identified only by the token from C1/C2 (SRS 5.1). */
  private String player(String authorization) {
    String player = sessions.playerForToken(authorization.replace("Bearer ", "").trim());
    if (player == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    return player;
  }
}
