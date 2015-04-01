import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Author:junjie
 * Create:27/3/15.
 * Target:<<
 */
public class ByteBufLifecycleTest {
    @Test
    public void cycle() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT
//                .buffer()
                .heapBuffer()
                .alloc().buffer(512);
        assertEquals(1, buf.refCnt());

        buf.retain(2);
        assertEquals(2 + 1, buf.refCnt());

        boolean destroyed = buf.release(2);
        if (destroyed) {
            assertEquals(0, buf.refCnt());
        }

        ByteBuf out = who_a(who_b(who_c(buf)));
        assertTrue(out.isReadable());
    }

    @Test
    public void derived() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT
                .heapBuffer()
                .alloc().buffer(512);
        assertEquals(1, buf.refCnt());

        ByteBuf duplicated = buf.duplicate();
        assertEquals(1, duplicated.refCnt());

        ByteBuf sliced = duplicated.slice();
        assertEquals(1, sliced.refCnt());

        buf.retain();
        assertEquals(2, sliced.refCnt());

        ByteBuf copied = sliced.copy();
        assertEquals(1, copied.refCnt());
        copied.release();
        assertEquals(0, copied.refCnt());

        assertEquals(2, sliced.refCnt());
    }

    @Test
    public void index() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT
                .heapBuffer()
                .alloc().buffer(16);
        assertEquals(0, buf.writerIndex());
        assertEquals(0, buf.readerIndex());

        buf.writeBytes("abc".getBytes(CharsetUtil.UTF_8));
        assertEquals(0, buf.readerIndex());
        assertEquals("abc".getBytes(CharsetUtil.UTF_8).length, buf.writerIndex());

        System.out.println(buf.getByte(0));
        assertEquals(0, buf.readerIndex());

        final Random random = new Random();
        while (buf.writableBytes() > 0) {
            buf.writeByte((char)(random.nextInt('z' - 'd') + 'd'));
        }

        assertEquals(buf.capacity(), buf.writerIndex());
        int capacity = buf.capacity();
        buf.writeByte('z');
        assertEquals(4*capacity, buf.capacity());

        buf.clear();
        assertEquals(0, buf.readerIndex());
        assertEquals(0, buf.writerIndex());
    }

    private ByteBuf who_a(ByteBuf in) {
        System.out.println(in);
        assertEquals(1, in.refCnt());
        in.writeBytes("who_a".getBytes(CharsetUtil.UTF_8));
        assertEquals(1, in.refCnt());

        System.out.println(in.toString(CharsetUtil.UTF_8));
        boolean destroyed = in.release();
        if (destroyed) {
            assertEquals(0, in.refCnt());
        }

        return in;
    }

    private ByteBuf who_b(ByteBuf in) {
        assertEquals(1, in.refCnt());
        in.writeBytes("who_b<".getBytes(CharsetUtil.UTF_8));
        try {
            ByteBuf out = in.alloc()
                    .heapBuffer
//                    .buffer //PooledUnsafeDirectByteBuf(ridx: 0, widx: 13, cap: 13)
                            (in.readableBytes() + 1);
            out.writeBytes(in);
            out.writeByte(42);
            return out;
        } finally {
            in.release();
        }
    }

    private ByteBuf who_c(ByteBuf in) {
        assertEquals(1, in.refCnt());
        in.writeBytes("who_c<".getBytes(CharsetUtil.UTF_8));
        in.retain();
        boolean destroyed = in.release();

        if (destroyed) {
            assertEquals(0, in.refCnt());
        }

        return in;
    }
}
