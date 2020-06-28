import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * USER: frankcbliu
 * DATE: 2020/6/21
 * TITLE:
 */

public class Main {
    // 1G =  1024 MB = 1024 * 1024 KB = 1024 * 1024 * 1024 B
    public static final byte[] memoryCache = new byte[1024 * 1024 * 1024];

    public static final int MAX_INDEX = 1024 * 1024 * 1024 / 256;

    // LRU 缓存
    static LRUCache lruCache = new LRUCache(MAX_INDEX);

    // 端口号
    private static final int PORT = 8888;

    public static PrintWriter writer;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("服务端已经启动...");
        Socket socket = server.accept();
        // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
        writer = new PrintWriter(socket.getOutputStream());
        // 创建线程池
        ExecutorService pool = Executors.newFixedThreadPool(8);

        for (int i = 0; i < 3; i++) {
            pool.submit(new Handler(socket.getInputStream(), true));
        }

    }

    /**
     * 发送信息到客户端
     *
     * @param line 信息
     */
    public static void send(String line) {
        writer.print(line);
        writer.flush();
    }
}

