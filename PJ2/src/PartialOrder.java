import java.io.*;
import java.util.BitSet;

/**
 * Created by Administrator on 2016/11/11.
 */
public class PartialOrder {
    private static int[][] data;
    private static int[] ranking;
    private static int[] tempRanking;
    private static int[] num2Rank, n2r2;
    private static int[] result;
    private static int l;
    private static BITree biTree;

    //private static final int RADIX_THRESHOLD = 8192;

    public static void main(String[] args) {
        //long time1 = 0, time2 = 0;
        for (int i = 0; i < 1; i++) {
            //time1 +=
            //run(new File("test/input5.txt"), new File("test/myOutput5.txt"), 0);
            run(new File("test/input5.txt"), new File("test/myOutput5.txt"), 1);
            //time2 += run(new File("test/input8.txt"), new File("test/myOutput8-" + i + ".txt"));
        }
        //System.out.println("\n" + time1 / 5 + "\n" + time2 / 5);
    }

    private static long run(File in, File out, int algo3) {
        long time = System.currentTimeMillis();
        int k = 1;
        l = 0;

        // Read
        try (LineReader reader = new LineReader(new FileInputStream(in))) {
            l = reader.readTillSpace();
            ranking = new int[l];
            result = new int[l];
            k = reader.readTillSpace();
            data = new int[k][l];
            int j;
            for (int i = 0; i < l; i++)
                for (j = 0; j < k; j++)
                    data[j][i] = reader.readTillSpace();
            switch (k) {
                case 3:
                    //tempRanking = new int[RADIX_THRESHOLD];
                    tempRanking = new int[l];
                    n2r2 = new int[l];
                case 2:
                    biTree = new BITree(l);
                    num2Rank = new int[l];
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        // Solve
        if (k == 9 || (k == 3 && algo3 == 0)) solveBlock(k);
        else {
            sort(data[0], ranking);
            if (k == 1)
                getNum2Rank(data[0], ranking, result);
            else {
                getNum2Rank(data[0], ranking, num2Rank);
                sort(data[1], ranking);
                if (k == 2)
                    getResult2();
                else {
                    getNum2Rank(data[1], ranking, n2r2);
                    sort(data[2], ranking);
                    getResult3(0, l - 1);
                }
            }
        }

        // Write
        try (LineWriter writer = new LineWriter(new FileOutputStream(out))) {
            for (int i = 0; i < l; i++) writer.writeLine(result[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        time = System.currentTimeMillis() - time;
        System.out.print(time + " ");
        return time;
    }

    /**
     * Solves multi-dimensional problem using block algorithm
     *
     * @param x number of dimensions
     */
    private static void solveBlock(int x) {
        int[][] rankingX = new int[x][l], num2RankX = new int[x][l];
        int blocks = (int) Math.sqrt(l);
        int numPerBlock = l / blocks + 1;
        BitSet[][] bitSet = new BitSet[x][blocks];
        BitSet[] bitResult = new BitSet[x];b
        int[] belongsTo = new int[l], numTillBlock = new int[blocks];

        int i, j, k = 0, t = 0, block, r;
        for (i = 0; i < x; i++) {
            sort(data[i], rankingX[i]);
            getNum2Rank(data[i], rankingX[i], num2RankX[i]);
        }
        for (j = 0; j < blocks; j++) {
            numTillBlock[j] = k;
            k += numPerBlock;
            if (k > l) k = l;
            for (; t < k; t++)
                belongsTo[t] = j;
        }
        for (i = 0; i < x; i++) {
            t = 0;
            bitResult[i] = new BitSet();
            bitSet[i][0] = new BitSet();
            for (; t < numPerBlock; t++)
                bitSet[i][0].set(rankingX[i][t]);
            for (j = 1; j < blocks; j++) {
                bitSet[i][j] = new BitSet();
                bitSet[i][j].or(bitSet[i][j - 1]);
                k = t + numPerBlock;
                if (k > l) k = l;
                for (; t < k; t++)
                    bitSet[i][j].set(rankingX[i][t]);
            }
        }
        for (t = 0; t < l; t++) {
            for (i = 0; i < x; i++) bitResult[i].clear();
            for (i = 0; i < x; i++) {
                r = num2RankX[i][t];
                block = belongsTo[r];
                if (block != 0) bitResult[i].or(bitSet[i][block - 1]);
                for (k = numTillBlock[block]; k < r; k++)
                    bitResult[i].set(rankingX[i][k]);
            }
            for (i = 1; i < x; i++)
                bitResult[0].and(bitResult[i]);
            result[t] = bitResult[0].cardinality();
        }
    }

    /**
     * Gets a ranking table for a set of data
     * Uses radix / 8 sorting algorithm
     *
     * @param data    the data to be sorted
     * @param ranking the array to store the ranking table
     */
    private static void sort(int[] data, int[] ranking) {
        int i, j, rtok, k = 10, r = 3, numR = 1 << r, mod = numR - 1, t;
        for (i = 0; i < l; i++) ranking[i] = i;
        int[] count = new int[numR], tempRnk = new int[l];
        for (i = 0, rtok = 0; i < k; i++, rtok += r) {
            for (j = 0; j < numR; j++) count[j] = 0;
            for (j = 0; j < l; j++) {
                t = data[ranking[j]] >> rtok;
                count[t & mod]++;
            }
            for (j = 1; j < numR; j++) count[j] = count[j - 1] + count[j];
            for (j = l - 1; j >= 0; j--) {
                t = data[ranking[j]] >> rtok;
                tempRnk[--count[t & mod]] = ranking[j];
            }
            for (j = 0; j < l; j++) ranking[j] = tempRnk[j];
        }
    }

    /**
     * Generates projection from data number to ranking
     *
     * @param data    the sorted data
     * @param ranking the corresponding ranking table
     * @param result  the array to store the projection
     */
    private static void getNum2Rank(int[] data, int[] ranking, int[] result) {
        int t = 0;
        for (int i = 0; i < l; i++) {
            if (data[ranking[i]] != data[ranking[t]])
                t = i;
            result[ranking[i]] = t;
        }
    }

    /**
     * Solves 2-dimensional problem using BITree (formerly SegTree)
     * Requires preprocessed ranking data
     */
    private static void getResult2() {
        int t = 0;
        int j;
        //SegTree tree = new SegTree(l);
        result[ranking[0]] = 0;
        for (int i = 1; i < l; i++) {
            if (data[1][ranking[t]] != data[1][ranking[i]]) {
                for (j = t; j < i; j++)
                    biTree.add(num2Rank[ranking[j]]);
                t = i;
            }
            result[ranking[i]] = biTree.sum(num2Rank[ranking[i]]);
        }
    }

    /**
     * Solves 3-dimensional problem using divide-and-conquer algorithm
     * Requires preprocessed ranking data
     *
     * @param left  left end of the part being processed
     * @param right right end of the part being processed
     */
    private static void getResult3(int left, int right) {
        if (left == right) return;
        if (left + 1 == right) {
            if (n2r2[ranking[right]] > n2r2[ranking[left]]) {
                if (num2Rank[ranking[left]] < num2Rank[ranking[right]]) result[ranking[right]]++;
            } else {
                int t = ranking[left];
                ranking[left] = ranking[right];
                ranking[right] = t;
            }
            return;
        }
        int m = left + right >> 1;
        getResult3(left, m);
        getResult3(m + 1, right);
        int i;
        int j = m + 1;
        int t = 0;
        //SegTree tree = new SegTree(l);

        //biTree.clear();
        for (i = left; i <= m; i++) {
            while (j <= right && n2r2[ranking[j]] <= n2r2[ranking[i]]) {
                result[ranking[j]] += biTree.sum(num2Rank[ranking[j]]);
                tempiRanking[t++] = ranking[j];
                j++;
            }
            biTree.add(num2Rank[ranking[i]]);
            tempRanking[t++] = ranking[i];
        }
        if (j <= right)
            for (; j <= right; j++) {
                result[ranking[j]] += biTree.sum(num2Rank[ranking[j]]);
                tempRanking[t++] = ranking[j];
            }
        for (i--; i >= left; i--) biTree.minus(num2Rank[ranking[i]]);
        System.arraycopy(tempRanking, 0, ranking, left, right - left + 1);
    }
}
