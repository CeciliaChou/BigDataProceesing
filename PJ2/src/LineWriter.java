import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/11/17.
 */
class LineWriter extends OutputStream {
    private static final int BUFFER_SIZE = 524288;
    private static final int[] DIGIT_SEPARATOR = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

    private OutputStream out;
    private byte[] buf = new byte[BUFFER_SIZE];
    private int pos;

    private int digits(int v) {
        for (int i = 0; ; i++)
            if (v <= DIGIT_SEPARATOR[i])
                return i + 1;
    }

    LineWriter(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    void writeLine(int v) throws IOException {
        int digits = digits(v) + 1;
        byte[] dis = new byte[digits];
        dis[digits - 1] = 0xa;
        for (int i = digits - 2; i > 0; i--) {
            dis[i] = (byte) (v % 10 + 0x30);
            v = (int) (((long)v * 0xCCCCCCCDL) >>> 35); // v /= 10
        }
        dis[0] = (byte) (v + 0x30);
        if (digits + pos >= BUFFER_SIZE) {
            System.arraycopy(dis, 0, buf, pos, BUFFER_SIZE - pos);
            out.write(buf);
            System.arraycopy(dis, BUFFER_SIZE - pos, buf, 0, digits + pos - BUFFER_SIZE);
            pos += digits - BUFFER_SIZE;
        } else {
            System.arraycopy(dis, 0, buf, pos, digits);
            pos += digits;
        }
    }

    @Override
    public void flush() throws IOException {
        out.write(buf, 0, pos);
    }

    @Override
    public void close() throws IOException {
        flush();
    }
}
