import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class E2EEChat
{
    private Socket clientSocket = null;

    public Socket getSocketContext() {
        return clientSocket;
    }

    // 접속 정보, 필요시 수정
    private final String hostname = "homework.islab.work";
    private final int port = 8080;

    public E2EEChat() throws IOException {
       clientSocket = new Socket();
       clientSocket.connect(new InetSocketAddress(hostname, port));

       InputStream stream = clientSocket.getInputStream();

       Thread senderThread = new Thread(new MessageSender(this));
       senderThread.start();

       while (true) {
           try {
               if (clientSocket.isClosed() || !senderThread.isAlive()) {
                   break;
               }

               byte[] recvBytes = new byte[2048];
               int recvSize = stream.read(recvBytes);

               if (recvSize == 0) {
                   continue;
               }

               String recv = new String(recvBytes, 0, recvSize, StandardCharsets.UTF_8);

               parseReceiveData(recv);
           } catch (IOException ex) {
               System.out.println("소켓 데이터 수신 중 문제가 발생하였습니다.");
               break;
           }
       }

       try {
           System.out.println("입력 스레드가 종료될때까지 대기중...");
           senderThread.join();

           if (clientSocket.isConnected()) {
               clientSocket.close();
           }
       } catch (InterruptedException ex) {
           System.out.println("종료되었습니다.");
       }
    }

    public void parseReceiveData(String recvData) {
        // 여기부터 3EPROTO 패킷 처리를 개시합니다.
        System.out.println(recvData + "\n==== recv ====");
    }

    // 필요한 경우 추가로 메서드를 정의하여 사용합니다.

    public static void main(String[] args)
    {
        try {
            new E2EEChat();
        } catch (UnknownHostException ex) {
            System.out.println("연결 실패, 호스트 정보를 확인하세요.");
        } catch (IOException ex) {
            System.out.println("소켓 통신 중 문제가 발생하였습니다.");
        }
    }
}

// 사용자 입력을 통한 메세지 전송을 위한 Sender Runnable Class
// 여기에서 메세지 전송 처리를 수행합니다.
class MessageSender implements Runnable {
    E2EEChat clientContext;
    OutputStream socketOutputStream;

    public MessageSender(E2EEChat context) throws IOException {
        clientContext = context;

        Socket clientSocket = clientContext.getSocketContext();
        socketOutputStream = clientSocket.getOutputStream();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("MESSAGE: ");

                String message = scanner.nextLine().trim();
                byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                socketOutputStream.write(payload, 0, payload.length);
            } catch (IOException ex) {
                break;
            }
        }

        System.out.println("MessageSender runnable end");
    }
}