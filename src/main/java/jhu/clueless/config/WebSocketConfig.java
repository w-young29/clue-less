
package jhu.clueless.config;

import jhu.clueless.websocket.ClueLessWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configures WebSocket support for the Clue-Less application.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ClueLessWebSocketHandler handler;

    /**
     * Creates the WebSocket configuration with the specified handler.
     *
     * @param handler the handler for WebSocket connections
     */
    public WebSocketConfig(ClueLessWebSocketHandler handler) {
        this.handler = handler;
    }

    /**
     * Registers the WebSocket endpoint used by clients to connect to the server.
     *
     * @param registry the registry used to register WebSocket handlers
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws");
    }
}