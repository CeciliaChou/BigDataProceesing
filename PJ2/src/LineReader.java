import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/11/11.
 */
class LineReader extends InputStream {
    private static final int BUFFER_SIZE = 524288;

    private InputStream in;
    private byte[] buf = new byte[BUFFER_SIZE];
    private int pos, total;

    LineReader(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    int readTillSpace() throws IOException {
        if (total == -1) return 0;
        int v = -1;
        byte b;
        while (true) {
            if (pos == total) {
                if ((total = in.read(buf)) == -1)
                    return v;
                pos = 0;
            }
            b = buf[pos];
            pos++;
            if (b == 0xa || b == 0x20 || b == 0xd) {
                if (v == -1) continue;
                return v;
            }
            if (v == -1)
                v = 0;
            v *= 10;
            v += (b - 0x30);
        }
    }
}
