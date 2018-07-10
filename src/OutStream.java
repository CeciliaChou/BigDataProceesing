import java.io.IOException;
import java.io.OutputStream;

class OutStream extends OutputStream {
    private static final int SIZE = 1024 * 1024;
    private OutputStream out;
    private int[] BITS = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    private int pos;
    private byte[] buf = new byte[SIZE];

    OutStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int value) throws IOException {
        out.write(value);
    }

    void writeTxt(int value) throws IOException {
        int bits = bits(value) + 1;
        byte[] bit = new byte[bits];
        bit[bits - 1] = 0xa;

        for (int i = bits - 2; i > 0; i--) {
            bit[i] = (byte) (value % 10 + 0x30);
            value = (int) (((long) value * 0xCCCCCCCDL) >>> 35);
        }
        bit[0] = (byte) (value + 0x30);

        if (bits + pos >= SIZE) {
            System.arraycopy(bit, 0, buf, pos, SIZE - pos);
            out.write(buf);
            System.arraycopy(bit, SIZE - pos, buf, 0, bits + pos - SIZE);
            pos += bits - SIZE;
        } else {
            System.arraycopy(bit, 0, buf, pos, bits);
            pos += bits;
        }
    }

    private int bits(int value) {
        for (int i = 0; ; i++)
            if (value <= BITS[i])
                return i + 1;
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
