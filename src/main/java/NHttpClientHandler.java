import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by junjie on 3/5/2015.
 */
public class NHttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//    super.exceptionCaught(ctx, cause);
    cause.printStackTrace();
    ctx.close();
  }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;

            System.err.println("STATUS: " + response.status());
            System.err.println("VERSION: " + response.protocolVersion());
            System.err.println();

            if (!response.headers().isEmpty()) {
                for (CharSequence name: response.headers().names()) {
                    for (CharSequence value: response.headers().getAll(name)) {
                        System.err.println("HEADER: " + name + " = " + value);
                    }
                }
                System.err.println();
            }

            if (HttpHeaderUtil.isTransferEncodingChunked(response)) {
                System.err.println("CHUNKED CONTENT {");
            } else {
                System.err.println("CONTENT {");
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;

            System.err.print(content.content().toString(CharsetUtil.UTF_8));
            System.err.flush();

            if (content instanceof LastHttpContent) {
                System.err.println("} END OF CONTENT");
                ctx.close();
            }
        }
    }
}
