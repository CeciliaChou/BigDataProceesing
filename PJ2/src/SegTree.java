import java.util.Arrays;

/**
 * Created by Administrator on 2016/11/17.
 * Better be replaced by <tt>BITree</tt> for this PJ
 */
@Deprecated
public class SegTree {
    private int[] tree;
    private int size;

    public SegTree(int size) {
        this.size = size;
        tree = new int[(size << 2) - size];
    }

    public void add(int pos) {
        int u = 1;
        int l = 0, r = size - 1, m;
        do {
            tree[u - 1]++;
            m = (l + r) >>> 1;
            u <<= 1;
            if (pos > m) {
                l = m + 1;
                u++;
            } else r = m;
        } while (l != r);
        tree[u - 1]++;
    }

    public int rangeZeroTo(int pos) {
        int sum = 0, u = 1;
        int l = 0, r = size - 1, m;
        int[] stack = new int[384];
        int pt = 0;
        stack[pt++] = u;
        stack[pt++] = l;
        stack[pt++] = r;
        do {
            r = stack[--pt];
            l = stack[--pt];
            u = stack[--pt];
            if (l >= pos) continue;
            if (r < pos) {
                sum += tree[u - 1];
                continue;
            }
            u <<= 1;
            m = l + r >>> 1;
            stack[pt++] = u | 1;
            stack[pt++] = m + 1;
            stack[pt++] = r;
            stack[pt++] = u;
            stack[pt++] = l;
            stack[pt++] = m;
        } while (pt != 0);
        return sum;
    }

    public void clear() {
        Arrays.fill(tree, 0);
    }
}
