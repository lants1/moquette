package io.moquette.fce.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * ByteBufferInputStream Wrapper
 * 
 * acording to:
 * http://stackoverflow.com/questions/4332264/wrapping-a-bytebuffer-with-an-inputstream/6603018#6603018
 *
 */
public class ByteBufferInputStream extends InputStream {

    ByteBuffer buf;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int off, int len)
            throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }
}