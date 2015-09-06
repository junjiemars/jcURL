import com.xws.nio.base.*;
import com.xws.nio.client.*;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:junjie
 * Create:27/3/15.
 * Target:<>
 */
public class ConcurrentNioTest {

    // gradle -Dtest.single=ConcurrentNio test
    @Test
    public void concurrent() {
        final int size = H.str_to_int(System.getProperty("test.size"), 1);
        final String url = System.getProperty("test.url");
        final String data = System.getProperty("test.data");

        final ExecutorService e = Executors.newFixedThreadPool(size);
        final Set<Callable<Integer>> tasks = new HashSet<Callable<Integer>>();

        for (int i = 0; i < size; i++) {
            tasks.add(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    _l.info(H.pad_right(String.format("##<call:%d>", H.tid()), A.OPTION_PROMPT_LEN, "="));
                    return ConcurrentNioTest.call(url, data);
                }
            });
        }

        try {
            e.invokeAll(tasks);
        } catch (InterruptedException ex) {
            _l.error(String.format("$$%s", ex));
        } finally {
            e.shutdown();
        }
    }

    private static Integer call(String url, String data) {
        NClient.request(
//                new RequestBuilder(url, data) {
//                    @Override
//                    protected FullHttpRequest setup(FullHttpRequest request) {
//                        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
//                                .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
//                                .set(HttpHeaderNames.CONTENT_TYPE, TEXT_XML)
//                        ;
//                        return (request);
//                    }
//                },
                new PostRequestBuilder(url, data) {
                    @Override
                    public void setup(PostRequestBuilder builder) {
                        builder.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                                .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
                                .set(HttpHeaderNames.CONTENT_TYPE, TEXT_XML)
                        ;
                    }
                },
                new PipelineBuilder() {
                    @Override
                    protected void setup(ChannelPipeline pipeline) {
                        pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {
                            @Override
                            protected void process(String s) {
                                _l.info(H.pad_right(String.format("##<process:%d:%d:%s>#%d",
                                                H.tid(), s.length(), new Date(), _ai.incrementAndGet()),
                                        2 * A.OPTION_PROMPT_LEN, "="));
                            }
                        });
                    }

                }
        );

        return 1;
    }
    private static final Logger _l = LoggerFactory.getLogger(ConcurrentNioTest.class);
    private static AtomicInteger _ai = new AtomicInteger(0);
}
