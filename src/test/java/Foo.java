import com.xw.http.*;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

/**
 * Author: junjie
 * Date: 3/25/15.
 * Target: <>
 */
public class Foo {
    public static void main(String[] args) {
        final String url = "http://cn.bing.com";
        final String data = "Hello";
        final int timeout = 500;

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
                    public ChannelPipeline setup(ChannelPipeline pipeline) {
                        // default http content processing

                        pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {
                            @Override
                            protected Integer process(String s) {
                                System.out.println(s);
                                return (s.length());
                            }
                        });
                        return (pipeline);
                    }
                });
    }
}
