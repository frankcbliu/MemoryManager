import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * USER: frankcbliu
 * DATE: 2020/6/27
 * TITLE:
 */

public class Handler implements Runnable {

    BufferedReader reader;

    // 是否服务端
    boolean isServer;
    // LRU 缓存
    LRUCache lruCache = Main.lruCache;
    // 内存缓存
    byte[] memoryCache = Main.memoryCache;
    // 当前索引
    public static int index = 0;

    /**
     * @param inputStream 输入流
     * @param isServer    是否服务端
     */
    public Handler(InputStream inputStream, boolean isServer) {
        this.isServer = isServer;
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void run() {
        try {
            if (isServer) { // 判断是否是服务端
                serverReceive();
            } else {
                clientReceive();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 客户端接收方法
     */
    void clientReceive() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("[RECEIVE]: " + line);
        }
    }

    private static final int MAX_INDEX = 1024 * 1024 * 1024 / 256;


    /**
     * 服务端接收方法
     */
    void serverReceive() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            char method = line.charAt(0);
            line = line.substring(2);
            if (method == 'W') { // 写入请求
                // 计算 id
                int idIndex = line.indexOf(",");
                int id = Integer.parseInt(line.substring(0, idIndex));
                // 计算 size
                line = line.substring(idIndex + 1);
                int sizeIndex = line.indexOf(",");
                int size = Integer.parseInt(line.substring(0, sizeIndex));
                // 计算 num
                line = line.substring(sizeIndex + 1);
                int num = (int) Math.ceil(size / 256.0);
                // info -> LRU缓存
                lruCache.put(id, size, index, num);
                // 写入缓存
                byte[] data = line.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(data, 0, memoryCache, index * 256, data.length);
                if (index >= MAX_INDEX) { // 缓冲区全部被占用
                    Main.send(id + ",FULL\n");
                } else {
                    index += num;
                    Main.send(id + ",ok\n");
                }
            } else { // 读取请求
                int id = Integer.parseInt(line);
                InfoNode info = lruCache.get(id);
                if (info == null) { // 未曾写入
                    // 返回 FFFFFFFF
                    Main.send("FFFFFFFF\n");
                } else {
                    String data = new String(memoryCache, info.index * 256, info.size);
                    Main.send(info.id + "," + info.size + "," + data + "\n");
                }
            }
        }
    }
}
