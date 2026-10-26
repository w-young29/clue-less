
package jhu.clueless.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Handles WebSocket connections for the Clue-Less game.
 */
@Component
public class ClueLessWebSocketHandler extends TextWebSocketHandler {

    /**
     * Sends a welcome message when a client connects to the server.
     *
     * @param session the newly established WebSocket session
     * @throws IOException if sending the message fails
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
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
        System.out.println("Client disconnected");
    }

    /**
     * Sends a text message to a specific connected client.
     *
     * @param session the WebSocket session to send the message to
     * @param message the message to send to the client
     * @throws IOException if sending the message fails
     */
    public void sendMessage(WebSocketSession session, String message) throws IOException {
        session.sendMessage(new TextMessage(message));
    }
}