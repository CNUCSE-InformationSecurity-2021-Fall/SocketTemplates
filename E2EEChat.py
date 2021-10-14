import socket
import threading

# 서버 연결정보; 자체 서버 실행시 변경 가능
SERVER_HOST = "homework.islab.work"
SERVER_PORT = 8080

connectSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
connectSocket.connect((SERVER_HOST, SERVER_PORT))

def socket_read():
    while True:
        readbuff = connectSocket.recv(2048)

        if len(readbuff) == 0:
            continue

        recv_payload = readbuff.decode('utf-8')
        parse_payload(recv_payload)

def socket_send():
    while True:
        str = input("MESSAGE: ")
        send_bytes = str.encode('utf-8')

        connectSocket.sendall(send_bytes)

def parse_payload(payload):
    # 수신된 페이로드를 여기서 처리; 필요할 경우 추가 함수 정의 가능
    print(payload)
    pass

reading_thread = threading.Thread(target=socket_read)
sending_thread = threading.Thread(target=socket_send)

reading_thread.start()
sending_thread.start()

reading_thread.join()
sending_thread.join()