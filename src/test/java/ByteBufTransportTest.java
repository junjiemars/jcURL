import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.CharsetUtil;
import org.junit.Test;

/**
 * Author: junjie
 * Date: 4/1/15.
 * Target: <>
 */
public class ByteBufTransportTest {

    @Test
    public void transport() {
        Out o = new Out(512);
        In i = new In();

        i.read(o._b);
    }

    class Out {
        public Out(int size) {
            _b = PooledByteBufAllocator.DEFAULT
                    .buffer()
                    .alloc()
                    .buffer(size);
        }

        private final ByteBuf _b;
    }

    class In {
        public In() {

        }

        public void read(ByteBuf b) {
            System.out.println(b.toString(CharsetUtil.UTF_8));
        }
    }
}
