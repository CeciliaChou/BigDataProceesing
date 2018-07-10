import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.BitSet;

public class PJ02 {
    private static int[][] data;
    private static int[] rank;
    private static int[] tmpRank;
    private static int[] result;
    private static int num;
    private static myBIT tree;

    public static void main(String[] args) {
        for (int i = 1; i <= 8; i++) {
            long start = System.currentTimeMillis();
            File input = new File("test/input" + i + ".txt");
            File output = new File("test/myoutput" + i + ".txt");
            fun(input, output);
            System.out.println(1.0 * (System.currentTimeMillis() - start) / 1000 + "s");
        }
    }

    private static void fun(File input, File output) {
        int count = 1;
        num = 0;
        try (InStream ins = new InStream(new FileInputStream(input))) {
            num = ins.readTxt(); //people;
            rank = new int[num];
            result = new int[num];
            count = ins.readTxt(); //course number;
            data = new int[count][num];

            //read score;
            for (int i = 0; i < num; i++) {
                for (int j = 0; j < count; j++) {
                    data[j][i] = ins.readTxt();
                }
            }

            //condition 2;
            if (count == 2) {
                tree = new myBIT(num);
                tmpRank = new int[num];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (count == 3 || count == 9) {
            solution3and9(count);
        } else {
            //对第一门科目进行排序
            solution1(data[0], rank);
            if (count == 1)
                //一维，整理重复情况，直接可以得到答案；
                getRank(data[0], rank, result);
            else {
                getRank(data[0], rank, tmpRank); //对一维整理重复情况
                solution1(data[1], rank);
                solution2();
            }
        }

        try (OutStream os = new OutStream(new FileOutputStream(output))) {
            for (int i = 0; i < num; i++)
                os.writeTxt(result[i]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //solution 1： radix sort；
    private static void solution1(int[] data, int[] rank) {
        int i, j, rtok, tmp;
        int k = 10, r = 3, radix = 1 << r;
        for (i = 0; i < num; i++) rank[i] = i;
        int[] count = new int[radix];
        int[] rank1 = new int[num];     //tmp rank result;
        for (i = 0, rtok = 0; i < k; i++, rtok += r) {
            for (j = 0; j < radix; j++)
                count[j] = 0;
            for (j = 0; j < num; j++) {
                tmp = data[rank[j]] >> rtok;
                count[tmp - (tmp >> r << r)]++;
            }
            for (j = 1; j < radix; j++)
                count[j] = count[j - 1] + count[j];
            for (j = num - 1; j >= 0; j--) {
                tmp = data[rank[j]] >> rtok;
                rank1[--count[tmp - (tmp >> r << r)]] = rank[j];
            }
            for (j = 0; j < num; j++)
                rank[j] = rank1[j];
        }
    }

    //处理一维情况下的排名重合； rank[超过的人数]= 学生序号 -> result[学生序号]=超过人数；
    private static void getRank(int[] data, int[] rank, int[] result) {
        int tmp = 0;
        for (int i = 0; i < num; i++) {
            if (data[rank[tmp]] != data[rank[i]])
                tmp = i;
            result[rank[i]] = tmp;
        }
    }

    private static void solution2() {
        int tmp = 0;
        result[rank[0]] = 0;
        //从1开始；
        for (int i = 1; i < num; i++) {
            if (data[1][rank[tmp]] != data[1][rank[i]]) {
                for (int j = tmp; j < i; j++) {
                    tree.update(tmpRank[rank[j]]);
                }
                tmp = i;
            }
            result[rank[i]] = tree.read(tmpRank[rank[i]]);
        }
    }

    private static void solution3and9(int course) {
        int[][] rankNum = new int[course][num];
        int[][] tempRank = new int[course][num];
        int blkCnt = (int) Math.sqrt(num);
        int elePerblk = num / blkCnt + 1;
        BitSet[][] bitSet = new BitSet[course][blkCnt];
        BitSet[] bitResult = new BitSet[course];
        int curEleCnt[] = new int[blkCnt];
        int blkContains[] = new int[num];

        //sort for each dimension
        for (int i = 0; i < course; i++) {
            solution1(data[i], rankNum[i]);
            getRank(data[i], rankNum[i], tempRank[i]);
        }

        //determine element belongs to which block
        int tmp = 0, m = 0;
        for (int i = 0; i < blkCnt; i++) {
            curEleCnt[i] = tmp;
            tmp += elePerblk;
            if (tmp > num)
                tmp = num;
            for (; m < tmp; m++)
                blkContains[m] = i;
        }

        for (int i = 0; i < course; i++) {
            m = 0;
            bitResult[i] = new BitSet();
            bitSet[i][0] = new BitSet();
            for (; m < elePerblk; m++) {
                //set per ele
                bitSet[i][0].set(rankNum[i][m]);
            }

            for (int k = 1; k < blkCnt; k++) {
                bitSet[i][k] = new BitSet();
                //copy bit;
                bitSet[i][k].or(bitSet[i][k - 1]);
                tmp = m + elePerblk;
                if (tmp > num)
                    tmp = num;
                for (; m < tmp; m++)
                    bitSet[i][k].set(rankNum[i][m]);
            }
        }

        int n, blk;
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < course; j++)
                bitResult[j].clear();
            for (int j = 0; j < course; j++) {
                n = tempRank[j][i];
                blk = blkContains[n];
                if (blk != 0)
                    bitResult[j].or(bitSet[j][blk - 1]);
                for (int k = curEleCnt[blk]; k < n; k++) {
                    bitResult[j].set(rankNum[j][k]);
                }
            }
            for (int j = 1; j < course; j++) {
                bitResult[0].and(bitResult[j]);
            }
            result[i] = bitResult[0].cardinality();
        }
    }
}