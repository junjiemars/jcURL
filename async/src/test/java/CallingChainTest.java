import com.xws.nio.base.*;
import com.xws.nio.client.*;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: junjie
 * Date: 3/25/15.
 * Target: <>
 */
public class CallingChainTest {
    @Test
    public void calling() {
        callA("http://cn.bing.com", "Hello", 500);
    }

    private static void callA(final String url, final String data, final int timeout) {
        NClient.request(new PostRequestBuilder(url, data) {
                            @Override
                            public void setup(final PostRequestBuilder builder) {
                                builder.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                                        .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
                                        .set(HttpHeaderNames.CONTENT_TYPE, TEXT_XML);
                            }
                        },
                new PipelineBuilder(timeout) {
                    @Override
                    public void setup(final ChannelPipeline pipeline) {
                        // default http content processing

                        pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {
                            @Override
                            protected void process(String s) {
                                _l.info(H.pad_right(String.format("##<callB|Tid:%d>",
                                        H.tid()), A.OPTION_PROMPT_LEN, "#"));
                                _l.info(s);

                                // conditioning
                                callB(s.length(), "http://www.baidu.com", "Welcome", 500);
                            }
                        });
                    }
                });
    }

    private static void callB(final int aLen, final String url, final String data, final int timeout) {
        NClient.request(new PostRequestBuilder(url, data) {
                            @Override
                            public void setup(final PostRequestBuilder builder) {
                                // customize the http headers
                                builder.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                                        .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
                                        .set(HttpHeaderNames.CONTENT_TYPE, TEXT_XML);
                            }
                        },
                new PipelineBuilder(timeout) {
                    @Override
                    public void setup(final ChannelPipeline pipeline) {
                        // default http content processing
                        pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {
                            @Override
                            protected void process(String s) {
                                _l.info(H.pad_right(String.format("##<callB|Tid:%d>",
                                        H.tid()), A.OPTION_PROMPT_LEN, "#"));
                                _l.info(s);
                                _l.info(String.format("<A:%d+B:%d=%d>", aLen, s.length(), aLen + s.length()));
                            }
                        });
                    }
                });
    }

    private static final Logger _l = LoggerFactory.getLogger(CallingChainTest.class);
}
