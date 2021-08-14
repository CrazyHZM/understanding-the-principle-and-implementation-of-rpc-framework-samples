package samples.communication.javanio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @author: huazhongming
 * @Date: Created in 2020/9/21 10:28 上午
 */
public class Client {

    private final String serverHost;
    private final int serverPort;
    private Selector selector;
    private SelectionKey selectionKey;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SocketChannel client;

    public Client(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        // 获取一个客户端socket通道
        SocketChannel socketChannel = SocketChannel.open();
        // 设置socket为非阻塞方式
        socketChannel.configureBlocking(false);
        // 获取一个选择器
        selector = Selector.open();
        // 注册客户端socket到选择器
        selectionKey = socketChannel.register(selector, 0);
        // 发起连接
        boolean isConnected = socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        // 如果连接没有马上建立成功，则设置对连接完成事件感兴趣
        if (!isConnected) {
            selectionKey.interestOps(SelectionKey.OP_CONNECT);
        }
        selector.select();
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        selectionKey = iterator.next();
        iterator.remove();
        int readyOps = selectionKey.readyOps();
        if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
            client = (SocketChannel) selectionKey.channel();
        }
        executorService.execute(this::handleEvent);

    }


    private void sendMsg() throws IOException {
        // 等待客户端socket完成与服务器端的链接
        if (!client.finishConnect()) {
            throw new Error();
        }
        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
        sendBuffer.clear();
        sendBuffer.put("Hello server.".getBytes());
        sendBuffer.flip();
        // 写数据。
        client.write(sendBuffer);
        // 设置对读事件感兴趣
        if (selectionKey != null) {
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    private void handleEvent() {
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SocketChannel client;
                while (iterator.hasNext()) {
                    selectionKey = iterator.next();
                    iterator.remove();
                    int readyOps = selectionKey.readyOps();
                    if ((readyOps & SelectionKey.OP_CONNECT) != 0) {

                    } else if ((readyOps & SelectionKey.OP_READ) != 0) {
                        client = (SocketChannel) selectionKey.channel();
                        ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
                        receiveBuffer.clear();
                        int count = client.read(receiveBuffer);
                        if (count > 0) {
                            String msg = new String(receiveBuffer.array(), 0, count);
                            System.out.println("receive msg from server:" + msg);
                        }

                    }
                }
            }
        } catch (IOException e) {

        }

    }


    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 3333);
        client.connect();
        client.sendMsg();
    }
}
