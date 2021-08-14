package samples.communication.javaaio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020/9/21 2:57 下午
 */
public class Client {
    private final String serverHost;
    private final int serverPort;
    private AsynchronousSocketChannel clientChannel;

    public Client(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        try {
            clientChannel = AsynchronousSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(serverHost, serverPort);
            Future<Void> connect = clientChannel.connect(inetSocketAddress);

            while (!connect.isDone()) {
                Thread.sleep(10);
            }
        } catch (InterruptedException ignored) {

        }
    }

    private void sendMsg() throws Exception {

        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
        sendBuffer.clear();
        String sendContent = "Hello server.";
        sendBuffer.put(sendContent.getBytes());
        sendBuffer.flip();
        Future<Integer> write = clientChannel.write(sendBuffer);
        while (!write.isDone()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Future<Integer> read = clientChannel.read(buffer);
        while (!read.isDone()) {
            Thread.sleep(10);
        }
        System.out.println("client received data from server: " + new String(buffer.array(), 0, read.get()));
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client("127.0.0.1",3333);
        client.connect();
        client.sendMsg();
    }
}
