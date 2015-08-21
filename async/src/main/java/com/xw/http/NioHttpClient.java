package com.xw.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.concurrent.Executors;

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
                .group(_g/* = new NioEventLoopGroup(4)*/)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);

    }

    public N to(final String uri) throws URISyntaxException {
        _uri = new URI(uri);
        _b
                .remoteAddress(_uri.getHost(), (-1 == _uri.getPort()) ? 80 : _uri.getPort())
        ;
        return (N) this;
    }

    public final N post(final String out) throws InterruptedException {
        return post(out, CharsetUtil.UTF_8, "*/*");
    }

    public final N post(final String out, final Charset charset, final String accept) {
        _req = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, _uri.toString(),
                PooledByteBufAllocator.DEFAULT.buffer().writeBytes(out.getBytes(_cs = charset)));
        _req.headers()
                .set(HttpHeaderNames.ACCEPT_CHARSET, _cs)
                .set(HttpHeaderNames.USER_AGENT, _ua)
                .set(HttpHeaderNames.ACCEPT, new AsciiString(accept))
                .set(HttpHeaderNames.CONTENT_LENGTH, _req.content().readableBytes())
                .set(HttpHeaderNames.HOST, _uri.getHost())
        ;

        return (N) this;
    }

    public final N onReceive(final R in) throws InvalidParameterException, IllegalStateException, InterruptedException {
        if (null == in) {
            throw new InvalidParameterException("#R:in is invalid");
        }
        if (null == _uri || null == _req) {
            throw new IllegalStateException("#check calling order: to()->post()->onReceive()");
        }

        final StringBuilder buffer = new StringBuilder();

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

                                        if (in.progressive()) {
                                            in.onReceive(msg.content().toString(_cs));
                                        } else {
                                            buffer.append(msg.content().toString(_cs));
                                        }

                                        if (msg instanceof LastHttpContent) {
                                            if (!in.progressive()) {
                                                in.onReceive(buffer.toString());
                                            }
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

//                .channel().pipeline().addLast(SimpleChannelInboundHandler.class.getSimpleName(),
//                new SimpleChannelInboundHandler<HttpContent>() {
//            @Override
//            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
////                super.exceptionCaught(ctx, cause);
//                _l.error(cause.getMessage(), cause);
//                ctx.close();
//            }
//
//            @Override
//            protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
//                _l.debug("#------------------------------");
//                for (String n : ctx.pipeline().names()) {
//                    _l.debug("#handler-name:{}", n);
//                }
//                _l.debug("#------------------------------");
//
//                if (msg instanceof HttpResponse) {
//                    final HttpResponse response = (HttpResponse) msg;
//                    _l.debug(response.toString());
//                }
//
//                if (in.progressive()) {
//                    in.onReceive(msg.content().toString(_cs));
//                } else {
//                    buffer.append(msg.content().toString(_cs));
//                }
//
//                if (msg instanceof LastHttpContent) {
//                    if (!in.progressive()) {
//                        in.onReceive(buffer.toString());
//                    }
//                    ctx.close();
//                }
//
//
////                if (msg instanceof HttpContent) {
////
////                    final HttpContent c = (HttpContent) msg;
////
////                    if (in.progressive()) {
////                        in.onReceive(c.content().toString(_cs));
////                    } else {
////                        buffer.append(c.content().toString(_cs));
////                    }
////
////                    if (c instanceof LastHttpContent) {
////                        if (!in.progressive()) {
////                            in.onReceive(buffer.toString());
////                        }
////
////                        ctx.close();
////                    }
////
////                }
//            }
//        })

                .channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
//                _g.shutdownGracefully();

                in.onDone();
            }
        });
        return (N) this;
    }


//    @Override
//    public void close() throws IOException {
//
//    }

    private final static EventLoopGroup _g = new NioEventLoopGroup();
    private final static EventExecutorGroup _e = new DefaultEventExecutorGroup(16);
    private final static AsciiString _ua = new AsciiString("NioHttpClient");


    private final static Logger _l = LoggerFactory.getLogger(NioHttpClient.class);
}
