import serial
import socket
import json
import threading
import time

# ====== Settings ======
SERIAL_PORT = 'COM2'   # com0com pair - Python side
BAUD_RATE   = 9600
TCP_HOST    = '0.0.0.0'
TCP_PORT    = 5000

latest_data = {"temp": 0, "fan": 0, "threshold": 30}
clients     = []
clients_lock = threading.Lock()

# Global serial reference so tcp_handler can write to it
serial_conn = None

def serial_reader():
    global serial_conn
    while True:
        try:
            with serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=2) as ser:
                serial_conn = ser
                print(f"[SERIAL] Connected on {SERIAL_PORT}")
                while True:
                    line = ser.readline()
                    decoded = line.decode('utf-8', errors='ignore').strip()
                    if decoded:
                        try:
                            data = json.loads(decoded)
                            latest_data.update(data)
                            msg = json.dumps(latest_data) + '\n'
                            broadcast(msg)
                            print(f"[FROM PROTEUS] {decoded}")
                        except json.JSONDecodeError as e:
                            print(f"[JSON ERROR] {e} | raw: '{decoded}'")
        except serial.SerialException as e:
            serial_conn = None
            print(f"[SERIAL ERROR] {e} — retrying in 3s")
            time.sleep(3)

def broadcast(message):
    dead = []
    with clients_lock:
        for c in clients:
            try:
                c.sendall(message.encode())
            except:
                dead.append(c)
        for c in dead:
            clients.remove(c)

def tcp_handler(conn, addr):
    """
    Handles one Android client.
    Forwards any threshold command received from Android to Proteus via serial.
    """
    print(f"[TCP] Android connected from {addr}")
    with clients_lock:
        clients.append(conn)

    # Send latest data immediately on connect
    try:
        conn.sendall((json.dumps(latest_data) + '\n').encode())
    except:
        pass

    buffer = ""
    while True:
        try:
            chunk = conn.recv(1024).decode('utf-8', errors='ignore')
            if not chunk:
                break
            buffer += chunk
            while '\n' in buffer:
                line, buffer = buffer.split('\n', 1)
                line = line.strip()
                if not line:
                    continue
                print(f"[FROM ANDROID] {line}")
                try:
                    cmd = json.loads(line)
                    # Forward threshold command to Proteus
                    if "threshold" in cmd:
                        latest_data["threshold"] = cmd["threshold"]
                        if serial_conn and serial_conn.is_open:
                            forward = json.dumps({"threshold": cmd["threshold"]}) + '\n'
                            serial_conn.write(forward.encode())
                            print(f"[TO PROTEUS] {forward.strip()}")
                        else:
                            print("[WARNING] Serial not available, command not forwarded")
                except json.JSONDecodeError as e:
                    print(f"[JSON ERROR from Android] {e} | raw: '{line}'")
        except Exception as e:
            print(f"[TCP] Client {addr} error: {e}")
            break

    print(f"[TCP] Android disconnected: {addr}")
    with clients_lock:
        if conn in clients:
            clients.remove(conn)
    conn.close()

def tcp_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind((TCP_HOST, TCP_PORT))
    server.listen(5)
    print(f"[TCP] Server listening on port {TCP_PORT}")
    while True:
        conn, addr = server.accept()
        t = threading.Thread(target=tcp_handler, args=(conn, addr), daemon=True)
        t.start()

threading.Thread(target=serial_reader, daemon=True).start()
tcp_server()