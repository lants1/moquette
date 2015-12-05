package io.moquette.fce.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * ByteBufferOutputStream Wrapper
 * 
 * according to:
 * http://stackoverflow.com/questions/4332264/wrapping-a-bytebuffer-with-an-inputstream/6603018#6603018
 */
public class ByteBufferOutputStream extends OutputStream {
    ByteBuffer buf;

    public ByteBufferOutputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public void write(int b) throws IOException {
        buf.put((byte) b);
    }

    @Override
    public void write(byte[] bytes, int off, int len)
            throws IOException {
        buf.put(bytes, off, len);
    }

}