package com.xw.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;


/**
 * Created by junjie on 8/17/15.
 */
public final class NioHttpClient<N extends NioHttpClient<N, R>, R extends Receiver>  /*implements Closeable*/ {

    private final Bootstrap _b;
    //    private final EventLoopGroup _g;
    private URI _uri;
    private DefaultFullHttpRequest _req;
    private Charset _cs;


    public NioHttpClient() {
        _b = new Bootstrap()
                .group(_g/* = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors())*/)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);

    }

    public N to(final String uri) throws URISyntaxException {
        _uri = new URI(uri);
        _b.remoteAddress(_uri.getHost(), (-1 == _uri.getPort()) ? 80 : _uri.getPort())
        ;
        return (N) this;
    }

    public final N get() {
        return get(CharsetUtil.UTF_8, "*/*");
    }

    public final N get(final Charset charset, final String accept) {
        _req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, _uri.getRawPath());
        _req.headers()
                .set(HttpHeaderNames.ACCEPT_CHARSET, _cs = charset)
                .set(HttpHeaderNames.USER_AGENT, _ua)
                .set(HttpHeaderNames.ACCEPT, new AsciiString(accept))
                .set(HttpHeaderNames.HOST, _uri.getHost())
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

        return (N) this;
    }

    public final N post(final String out) throws InvalidParameterException {
        if (H.is_null_or_empty(out)) {
            throw new InvalidParameterException("#String:<out> is invalid");
        }

        return post(out, CharsetUtil.UTF_8, "*/*");
    }

    public final N post(final String out, final Charset charset, final String accept) throws InvalidParameterException {
        if (H.is_null_or_empty(out)) {
            throw new InvalidParameterException("#String:<out> is invalid");
        }

        _req = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, _uri.getRawPath(),
                PooledByteBufAllocator.DEFAULT.buffer().writeBytes(out.getBytes(_cs = charset)));

        _req.headers()
                .set(HttpHeaderNames.ACCEPT_CHARSET, _cs)
                .set(HttpHeaderNames.USER_AGENT, _ua)
                .set(HttpHeaderNames.ACCEPT, new AsciiString(accept))
                .set(HttpHeaderNames.CONTENT_LENGTH, _req.content().readableBytes())
                .set(HttpHeaderNames.HOST, _uri.getHost())
//                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
        ;

        return (N) this;
    }

    public final <T> N headers(final AsciiString name, final T value) throws InvalidParameterException {
        if (name == null) {
            throw new InvalidParameterException("AsciiString:<name> is invalid");
        }

        if (_req == null) {
            throw new IllegalStateException("#check calling order: to()->post()->onReceive()");
        }

        _req.headers().set(name, value);

        return (N) this;
    }

    public final N onReceive(final R in) throws InvalidParameterException, IllegalStateException, InterruptedException {
        if (null == in) {
            throw new InvalidParameterException("#R:<in> is invalid");
        }
        if (null == _uri || null == _req) {
            throw new IllegalStateException("#check calling order: to()->post()->onReceive()");
        }

        final StringBuffer buffer = new StringBuffer();

        _b.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(HttpClientCodec.class.getSimpleName(), new HttpClientCodec())
                        .addLast(HttpContentDecompressor.class.getSimpleName(), new HttpContentDecompressor())
                        .addLast(SimpleChannelInboundHandler.class.getSimpleName(),
                                new SimpleChannelInboundHandler<HttpContent>() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        _l.error(cause.getMessage(), cause);
                                        ctx.close();
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
//                                        for (String n : ctx.pipeline().names()) {
//                                            _l.debug("#handler-name:{}", n);
//                                        }
//                                        _l.debug("#------------------------------");

                                        if (msg instanceof HttpResponse) {
                                            final HttpResponse response = (HttpResponse) msg;
                                            _l.debug(response.toString());
                                        }

                                        buffer.append(msg.content().toString(_cs));

                                        if (msg instanceof LastHttpContent) {
                                            ctx.close();
                                        }
                                    }
                                });
            }
        })

                .connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().writeAndFlush(_req);
            }
        })

                .channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                in.onReceive(buffer.toString());
            }
        });
        return (N) this;
    }


//    @Override
//    public void close() throws IOException {
//
//    }

    // The default nThreads for EventLoopGroup is 2*Runtime.getRuntime().availableProcessors()
    private final static EventLoopGroup _g = new NioEventLoopGroup(/*Runtime.getRuntime().availableProcessors()*/);

//    private final static EventExecutorGroup _e = new DefaultEventExecutorGroup(200);

    private final static AsciiString _ua = new AsciiString("NioHttpClient");


    private final static Logger _l = LoggerFactory.getLogger(NioHttpClient.class);
}
