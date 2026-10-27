import json
import urllib.request

import websocket

# validate the client -> server (REST) and server -> client (WebSocket) paths

req = urllib.request.Request(
    "http://localhost:8080/api/games",
    data=json.dumps({"username": "tester"}).encode(),
    headers={"Content-Type": "application/json"},
    method="POST",
)
game = json.loads(urllib.request.urlopen(req).read())
print("C1 response:", game)

ws = websocket.create_connection(f"ws://localhost:8080/ws?gameId={game['gameId']}&playerId={game['playerId']}")
print("Server says:", ws.recv())
ws.close()
