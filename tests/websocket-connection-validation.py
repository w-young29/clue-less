import websocket

# validate websocket connection can be made due to no frontend code for now

ws = websocket.create_connection("ws://localhost:8080/ws")
print("Server says:", ws.recv())
ws.close()