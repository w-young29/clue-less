package jhu.clueless;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Skeletal architecture check: two clients connect, and messages sent over REST by one
 * client reach the other client over WebSocket as unicast and broadcast messages.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessagingIntegrationTest {

    @LocalServerPort
    int port;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().proxy(HttpClient.Builder.NO_PROXY).build();

    @Test
    void clientsReceiveUnicastAndBroadcastMessages() throws Exception {
        // C1: Alice creates a game, then opens her WebSocket
        JsonNode alice = post("/api/games", null, "{\"username\":\"Alice\"}");
        String gameId = alice.get("gameId").asText();
        BlockingQueue<JsonNode> aliceInbox = connect(gameId, alice.get("playerId").asText());

        // C2: Bob joins -> Alice receives B1 broadcast
        JsonNode bob = post("/api/games/" + gameId + "/players", null, "{\"username\":\"Bob\"}");
        assertEquals("B1_PLAYER_JOINED", next(aliceInbox).get("type").asText());
        BlockingQueue<JsonNode> bobInbox = connect(gameId, bob.get("playerId").asText());

        // C4: Alice starts -> both get the B4 broadcast and their own U1 hand
        post("/api/games/" + gameId + "/start", alice.get("token").asText(), "");
        assertEquals("B4_GAME_STARTED", next(bobInbox).get("type").asText());
        assertEquals("U1_HAND_DEALT", next(bobInbox).get("type").asText());
        assertEquals("B4_GAME_STARTED", next(aliceInbox).get("type").asText());
        assertEquals("U1_HAND_DEALT", next(aliceInbox).get("type").asText());

        // C6: Alice moves -> Bob receives the B6 broadcast with a sequence number
        post("/api/games/" + gameId + "/move", alice.get("token").asText(), "{\"to\":\"HALL\"}");
        JsonNode moved = next(bobInbox);
        assertEquals("B6_PLAYER_MOVED", moved.get("type").asText());
        assertEquals("HALL", moved.get("payload").get("to").asText());
        assertNotNull(moved.get("seq"));

        // C9: Bob accuses -> Alice gets the B12 broadcast; only Bob gets U5 with the result
        post("/api/games/" + gameId + "/accusation", bob.get("token").asText(),
            "{\"suspect\":\"PLUM\",\"weapon\":\"ROPE\",\"room\":\"HALL\"}");
        assertEquals("B6_PLAYER_MOVED", next(aliceInbox).get("type").asText());
        assertEquals("B12_ACCUSATION_MADE", next(aliceInbox).get("type").asText());
        assertEquals("B12_ACCUSATION_MADE", next(bobInbox).get("type").asText());
        assertEquals("U5_ACCUSATION_RESULT", next(bobInbox).get("type").asText());
        assertNull(aliceInbox.poll(500, TimeUnit.MILLISECONDS), "U5 must go to the accuser only");
    }

    private JsonNode post(String path, String token, String body) throws Exception {
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) req.header("Authorization", "Bearer " + token);
        HttpResponse<String> res = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, res.statusCode(), res.body());
        return mapper.readTree(res.body());
    }

    /** Opens a WebSocket and queues every JSON message received (skips the text welcome). */
    private BlockingQueue<JsonNode> connect(String gameId, String playerId) {
        BlockingQueue<JsonNode> inbox = new LinkedBlockingQueue<>();
        URI uri = URI.create("ws://localhost:" + port + "/ws?gameId=" + gameId + "&playerId=" + playerId);
        http.newWebSocketBuilder().buildAsync(uri, new WebSocket.Listener() {
            @Override
            public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                try {
                    inbox.add(mapper.readTree(data.toString()));
                } catch (Exception ignored) {
                    // welcome text, not JSON
                }
                ws.request(1);
                return null;
            }
        }).join();
        return inbox;
    }

    private JsonNode next(BlockingQueue<JsonNode> inbox) throws InterruptedException {
        JsonNode msg = inbox.poll(5, TimeUnit.SECONDS);
        assertNotNull(msg, "expected a message");
        return msg;
    }
}
