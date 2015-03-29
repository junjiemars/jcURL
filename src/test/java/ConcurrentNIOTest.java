import com.xw.http.*;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

/**
 * Author:junjie
 * Create:27/3/15.
 * Target:<>
 */
public class ConcurrentNIOTest {

    // gradle -Dtest.single=Concurrent test
    @Test
    public void concurrent() {
        final int size = 500;
        final ExecutorService e = Executors.newFixedThreadPool(size);
        final String url = "http://cn.bing.com";
        final String data = "Hello";
        final Set<Callable<Integer>> tasks = new HashSet<Callable<Integer>>();

        for (int i = 0; i < size; i++) {
            tasks.add(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    _l.info(H.pad_right(String.format("##<call:%d>", H.tid()), A.OPTION_PROMPT_LEN, "="));
                    return ConcurrentNIOTest.call(url, data);
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
        NClient.post(
                new RequestBuilder(url, data) {
                    @Override
                    protected FullHttpRequest setup(FullHttpRequest request) {
                        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                                .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
                                .set(HttpHeaderNames.CONTENT_TYPE, TEXT_XML)
                        ;
                        return (request);
                    }
                },
                new PipelineBuilder() {
                    @Override
                    protected ChannelPipeline setup(ChannelPipeline pipeline) {
                        pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {
                            @Override
                            protected Integer process(String s) {
                                _l.info(H.pad_right(String.format("##<process:%d:%d:%s>#%d",
                                                H.tid(), s.length(), new Date(), _ai.incrementAndGet()),
                                        2 * A.OPTION_PROMPT_LEN, "="));
                                return (s.length());
                            }
                        });
                        return (pipeline);
                    }

                }
        );

        return 1;
    }
    private static final Logger _l = LogManager.getLogger(ConcurrentNIOTest.class);
    private static AtomicInteger _ai = new AtomicInteger(0);
}
