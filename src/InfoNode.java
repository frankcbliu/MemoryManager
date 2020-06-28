/**
 * USER: frankcbliu
 * DATE: 2020/6/26
 * TITLE: 缓冲区块信息节点
 */

public class InfoNode {
    int id; // 数据ID
    int size; // 数据大小

    int index; // 缓存区块起始序号
    int num; // 缓冲区块使用数

    InfoNode pre;
    InfoNode next;

    public InfoNode(int id, int size, int index, int num) {
        this.id = id;
        this.size = size;
        this.index = index;
        this.num = num;
        pre = null;
        next = null;
    }

}
