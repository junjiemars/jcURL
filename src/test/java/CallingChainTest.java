import com.xw.http.*;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Author: junjie
 * Date: 3/25/15.
 * Target: <>
 */
public class CallingChainTest {
    @Test
    public void calling() {
        final String url = "http://cn.bing.com";
        final String data = "Hello";
        final int timeout = 500;

        callA("http://cn.bing.com", "Hello", 500);
    }

    private static void callA(final String url, final String data, final int timeout) {
        NClient.post(new RequestBuilder(url, data) {
                         @Override
                         public FullHttpRequest setup(FullHttpRequest request) {
                             // customize the http headers
                             request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                                     .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
                                     .set(HttpHeaderNames.CONTENT_TYPE, TEXT_XML)
                             ;
                             return (request);
                         }
                     },
                new PipelineBuilder(timeout) {
                    @Override
                    public ChannelPipeline setup(final ChannelPipeline pipeline) {
                        // default http content processing

                        pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {
                            @Override
                            protected Integer process(String s) {
                                _l.info(H.pad_right(String.format("##<callB|Tid:%d>",
                                        H.tid()), A.OPTION_PROMPT_LEN, "#"));
                                _l.info(s);

                                // conditioning
                                callB(s.length(), "http://www.baidu.com", "Welcome", 500);
                                return (s.length());
                            }
                        });
                        return (pipeline);
                    }
                });
    }

    private static void callB(final int aLen, final String url, final String data, final int timeout) {
        NClient.post(new RequestBuilder(url, data) {
                         @Override
                         public FullHttpRequest setup(FullHttpRequest request) {
                             // customize the http headers
                             request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                                     .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
                                     .set(HttpHeaderNames.CONTENT_TYPE, TEXT_XML)
                             ;
                             return (request);
                         }
                     },
                new PipelineBuilder(timeout) {
                    @Override
                    public ChannelPipeline setup(final ChannelPipeline pipeline) {
                        // default http content processing
                        pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {
                            @Override
                            protected Integer process(String s) {
                                _l.info(H.pad_right(String.format("##<callB|Tid:%d>",
                                        H.tid()), A.OPTION_PROMPT_LEN, "#"));
                                _l.info(s);
                                _l.info(String.format("<A:%d+B:%d=%d>", aLen, s.length(), aLen+s.length()));
                                return (s.length());
                            }
                        });
                        return (pipeline);
                    }
                });
    }

    private static final Logger _l = LogManager.getLogger(CallingChainTest.class);
}
