//BIT: 树状数组
//        tree[i]: 索引为i的BIT值
//        num^- : 整数num的补，即在num的二进制表示中，0换为1，1换成0。如：num=10101，则 num^- =01010
//         O(lg n);

class myBIT {
    private int size;
    private int[] tree;

    myBIT(int size) {
        this.size = size;
        tree = new int[size];
    }

    //改变某个位置的频率并且更新数组
    void update(int idx) {
        idx++;
        while (idx <= size) {
            tree[idx - 1]++;
            idx += lowbit(idx);
        }
    }


    //得到sum(1~idx)
    int read(int idx) {
        int sum = 0;
        while (idx > 0) {
            sum += tree[idx - 1];
            idx -= lowbit(idx);
        }
        return sum;
    }

    // 保留x的二进制最低位1的值。例如，1110保留最低位1即0010.
    private int lowbit(int x) {
        return x & (-x);
    }

}
