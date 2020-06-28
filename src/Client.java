import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * USER: frankcbliu
 * DATE: 2020/6/27
 * TITLE:
 */

public class Client {

    // IP地址
    private static final String HOST = "127.0.0.1";
    // 端口
    private static final int PORT = 8888;

    private static final int TIMES = 1000;

    public static void main(String[] args) throws Exception {
        // 与服务端建立连接
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();
        // 建立连接后获得输出流
        Handler handler = new Handler(socket.getInputStream(), false);
        new Thread(handler).start();

        int count = 0;
        long totalSize = 0;
        long start = System.currentTimeMillis();
        while (count++ < TIMES) {
            Random random = new Random();
            int id = random.nextInt(1000);
            String line;
            if (random.nextInt(2) == 0) { // 写操作
                line = randomStr();
                totalSize += line.length();
                line = "W:" + id + "," + line.length() + "," + line + "\n";
            } else { // 读操作
                line = "R:" + id + "\n";
            }
            outputStream.write(line.getBytes(StandardCharsets.UTF_8));
            if (line.equals("exit")) {
                break;
            }
            Thread.sleep(200);
        }
        long end = System.currentTimeMillis();

        System.out.println("循环次数：" + TIMES + " 数据量：" + totalSize / 1024 + "KB 耗时：" + (end - start) / 1000 + " s");

//        String[] msgs = new String[]{
//                "W:32,10,HelloWorld\n",
//                "R:32\n",
//                "W:4,10,sdadsfagas\n",
//                "W:2,8,emmm,six\n",
//                "W:3,10,sdadsfagas\n",
//                "R:32\n",
//        };
//        for (String message : msgs) {
//            System.out.print("[SEND]: " + message);
//            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
//            Thread.sleep(200);
//        }
//
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            System.out.print("[SEND]: ");
//            String line = scanner.nextLine() + "\n";
//            outputStream.write(line.getBytes(StandardCharsets.UTF_8));
//            if (line.equals("exit")) {
//                break;
//            }
//            Thread.sleep(200);
//        }
        socket.close();
    }

    /**
     * 随机生成字符串
     *
     * @return
     */
    public static String randomStr() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        int len = (int) (Math.random() * 5120); // 0 - 5k
        for (int i = 0; i < len; ++i) {
            int index = (int) (Math.random() * 52);// [0,51)
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }
}
