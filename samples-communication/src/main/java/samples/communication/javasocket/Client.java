package samples.communication.javasocket;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020/5/4 3:13 下午
 */
public class Client {

    private final String serverHost;
    private final int serverPort;
    private Socket socket;

    public Client(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void connect() {
        try {
            Socket socket = new Socket(serverHost, serverPort);
        } catch (IOException e) {
        }
    }
    public void request() {
        while (true) {
            try {
                if (socket == null) {
                    socket = new Socket(serverHost, serverPort);
                }
                socket.getOutputStream().write((new Date() + ": Hello server.").getBytes());
                socket.getOutputStream().flush();
                Thread.sleep(3000);
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1",3333);
        client.connect();
        client.request();
    }
}
