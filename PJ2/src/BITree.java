import java.util.Arrays;

/**
 * Created by Administrator on 2016/11/18.
 */
class BITree {
    private int[] tree;
    private int size;

    BITree(int size) {
        this.size = size;
        tree = new int[size];
    }

    void add(int pos) {
        pos++;
        while (pos <= size) {
            tree[pos - 1]++;
            pos += (pos & (-pos));
        }
    }

    void minus(int pos) {
        pos++;
        while (pos <= size) {
            tree[pos - 1]--;
            pos += (pos & (-pos));
        }
    }

    int sum(int pos) {
        int sum = 0;
        while (pos != 0) {
            sum += tree[pos - 1];
            pos -= (pos & (-pos));
        }
        return sum;
    }
}
