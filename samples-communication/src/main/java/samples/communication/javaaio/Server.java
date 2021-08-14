package samples.communication.javaaio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020/9/21 2:55 下午
 */
public class Server {

    private static final int DEFAULT_PORT = 3333;
    private final int port;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() throws Exception {
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        Future<AsynchronousSocketChannel> accept;
        while (true) {
            // 不会阻塞。
            accept = serverSocketChannel.accept();

            // 阻塞等待连接
            AsynchronousSocketChannel socketChannel = accept.get();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            socketChannel.read(buffer, buffer, new ReadHandler(socketChannel));

        }
    }

    static class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        private AsynchronousSocketChannel channel;

        public ReadHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer msg) {
            String body = new String(msg.array(), 0, result);
            System.out.println("server received data: " + body);

            ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
            // 发送数据到client
            sendBuffer.clear();
            String sendContent = "Hello client.";
            sendBuffer.put(sendContent.getBytes());
            sendBuffer.flip();
            Future<Integer> write = channel.write(sendBuffer);
            while (!write.isDone()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            System.out.println("response success.");

        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
        }
    }


}
