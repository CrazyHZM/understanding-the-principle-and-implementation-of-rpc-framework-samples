package samples.communication.javanio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020/9/21 10:27 上午
 */
public class Server {
    private Selector selector;

    private static final int DEFAULT_PORT = 3333;
    private final int port;
    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置socket为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 获取与该通道关联的服务端套接字
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 绑定服务端地址
        serverSocket.bind(new InetSocketAddress(port));
        // 获取一个选择器
        selector = Selector.open();
        // 注册通道到选择器，选择对OP_ACCEPT事件感兴趣
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 获取就绪的事件集合
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 处理就绪的事件
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                handleEvent(selectionKey);
            }
        }
    }

    private void handleEvent(SelectionKey selectionKey) throws IOException {
        SocketChannel client;
        // 如果是连接事件
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            client = server.accept();
            if (null == client) {
                return;
            }
            // 该套接字为非阻塞模式
            client.configureBlocking(false);
            // 把channel注册selector上，并且设置对OP_READ事件感兴趣
            client.register(selector, SelectionKey.OP_READ);

            // 如果是读事件
        } else if (selectionKey.isReadable()) {
            client = (SocketChannel) selectionKey.channel();
            ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
            receiveBuffer.clear();
            int count = client.read(receiveBuffer);
            if (count > 0) {
                String receiveContext = new String(receiveBuffer.array(), 0, count);
                System.out.println("receive client msg: " + receiveContext);
            }
            ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
            // 发送数据到client
            sendBuffer.clear();
            client = (SocketChannel) selectionKey.channel();
            String sendContent = "Hello client.";
            sendBuffer.put(sendContent.getBytes());
            sendBuffer.flip();
            client.write(sendBuffer);
            System.out.println("send msg to client:" + sendContent);

        }

    }

    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.start();
    }
}
