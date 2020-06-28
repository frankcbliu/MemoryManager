import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * USER: frankcbliu
 * DATE: 2020/6/27
 * TITLE: LRU 缓存
 */


public class LRUCache {
    ConcurrentHashMap<Integer, InfoNode> map = new ConcurrentHashMap<>();
    InfoNode head;// 头结点
    InfoNode tail;// 尾节点
    int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }


    /**
     * 删除节点，并移动到最前面
     *
     * @param node
     */
    private void removeAndInsert(InfoNode node) {
        if (node == head) { // 头结点
            return;
        } else if (node == tail) { // 尾节点
            // 移出尾节点
            tail = tail.pre;
            tail.next = null;
        } else { // 普通节点
            // 从链表中删除
            node.pre.next = node.next;
            node.next.pre = node.pre;
        }
        // 插入头结点
        node.next = head;
        node.pre = null;
        head.pre = node;
        head = node;
    }

    /**
     * 获取 LRU 缓存值，并且更新到头部
     *
     * @param id 数据ID
     * @return
     */
    public synchronized InfoNode get(int id) {
        InfoNode node = map.get(id);
        if (node == null) {
            return null;
        } else {
            removeAndInsert(node);
            return node;
        }
    }


    /**
     * 插入 LRU 节点到头部，如果节点已经存在，则移动到头部即可
     *
     * @param id    数据ID
     * @param size  数据大小
     * @param index 位于缓冲区的起始位置
     * @param num   使用的缓冲区块数量
     */
    public synchronized void put(int id, int size, int index, int num) {
        // 判断是否首次插入
        if (head == null) {
            head = new InfoNode(id, size, index, num);
            tail = head;
            // 插入 map 中
            map.put(id, head);
        }
        // 判断节点是否存在
        InfoNode node = map.get(id);
        if (node != null) {
            // 更新节点值
            node.size = size;
            node.index = index;
            node.num = num;
            // 移动到头结点
            removeAndInsert(node);
        } else { // 节点为空
            // 创建新节点
            node = new InfoNode(id, size, index, num);
            // 判断是否超过容量
            if (map.size() >= capacity) {
                // map 中删除最后一个节点
                try {
                    saveToFile(map.get(tail.id));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                map.remove(tail.id);
                // 链表中删除最后一个节点
                tail = tail.pre;
                tail.next = null;
            }
            // 放入 HashMap 中
            map.put(id, node);
            // 插入头结点
            node.next = head;
            head.pre = node;
            head = node;
        }
    }

    private static final long G_SIZE = 1024 * 1024 * 1024;

    /**
     * 写入文件
     *
     * @param node 节点信息
     * @throws IOException
     */
    void saveToFile(InfoNode node) throws IOException {
        File file = new File("save.txt");
        if (!file.exists()) { // 判断文件是否存在
            file.createNewFile();
        }
        if (file.length() / G_SIZE > 2) {
            System.out.println("文件缓冲区达到 2G，不再写入");
            return;
        }
        FileWriter writer = new FileWriter(file, true);
        writer.write(node.id + "");
        writer.write(",");
        writer.write(node.size + "");
        writer.write(",");
        writer.write(new String(Main.memoryCache, node.index * 256, node.size));
        writer.write("\n");
        writer.flush();
        writer.close();
    }
}

