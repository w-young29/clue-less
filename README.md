# clue-less
COAST to COAST

Clone to your repository.
cd into the project.
Run the application with: mvn spring-boot:run

Build the project with: mvn clean install -U

OpenAPI documentation:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Skeletal increment

The skeletal increment shows the client and server subsystems from the SRS talking to each other.
Game rules are stubbed and will be added in the Minimal increment.

- Clients send requests (C1 create, C2 join, C4 start, C6 move, C9 accuse) to the server as REST calls (`controller/ClueLessController`).
- The server pushes unicast (U#) and broadcast (B#) messages back over WebSocket (`websocket/ClueLessWebSocketHandler`).
- Server components from SRS 2.3.2 are stubs in `server/`: ServerCommunication, GameSessionManager,
  GameLogic, GameStateManager, SuggestionAccusationManager. Each one logs when it is called.
- Test client: http://localhost:8080/client.html (open one tab per player). It shows every message sent and received.
- System log: the console shows each request, each component call, and each message sent.

Quick checks:
- `mvn test` runs an integration test with two clients.
- The remaining SRS messages are defined in `messaging/` and will be implemented in the Minimal increment.
- `python tests/websocket-connection-validation.py` (needs `pip install websocket-client`).
