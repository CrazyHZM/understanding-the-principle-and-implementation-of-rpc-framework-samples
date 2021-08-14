package samples.communication.javasocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int DEFAULT_PORT = 3333;
    private final int port;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // 一、创建ServerSocket对象
        ServerSocket serverSocket = new ServerSocket(this.port);
        while (true) {
            try {
                // 二、调用accept阻塞方法，直到获取新的连接请求
                Socket socket = serverSocket.accept();
                // 三、每一个新的客户端连接都需要创建一个线程，负责与客户端通信以及数据的读写。
                new Thread(() -> {
                    try {
                        byte[] data = new byte[1024];
                        // 四、获取输入流InputStream对象
                        InputStream inputStream = socket.getInputStream();
                        while (true) {
                            int len;
                            while ((len = inputStream.read(data)) != -1) {
                                System.out.println(new String(data, 0, len));
                            }
                        }
                    } catch (IOException e) {
                    }
                }).start();

            } catch (IOException e) {
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
    }
}
