import java.io.IOException;
import java.io.InputStream;

class InStream extends InputStream {
    private static final int SIZE = 1024 * 1024;
    private InputStream in;
    private int total;
    private int pos;
    private byte[] buf = new byte[SIZE];

    InStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    int readTxt() throws IOException {
        byte tmp;
        int value = -1;

        if (total == -1)
            return 0;

        while (true) {
            if (pos == total) {
                if ((total = in.read(buf)) == -1) {
                    return value;
                }
                pos = 0;
            }

            tmp = buf[pos++];

            if (tmp == 0xa || tmp == 0x20 || tmp == 0xd) {
                if (value == -1)
                    continue;
                return value;
            }

            if (value == -1)
                value = 0;

            value *= 10;
            value += tmp - 0x30;
        }
    }
}

