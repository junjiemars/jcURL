package com.xw.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by junjie on 8/17/15.
 */
public final class NioHttpClient<N extends NioHttpClient<N, O, I>, O, I>  /*implements Closeable*/ {

    private final Bootstrap b;

    public NioHttpClient() {
        b = new Bootstrap()
                .group(_g)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);
    }

    public final N to(final String url) throws URISyntaxException {
        final URI uri = new URI(url);
        b.remoteAddress(uri.getHost(), (-1 == uri.getPort()) ? 80 : uri.getPort())
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpClientCodec())
                                .addLast(new HttpContentDecompressor())
                                .addLast(new SimpleChannelInboundHandler<HttpObject>() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        super.exceptionCaught(ctx, cause);
                                        _l.error(cause.toString());
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                        _l.info(msg.toString());
                                    }
                                });
                    }
                });
        return (N)this;
    }

    public final N post(final O out) throws InterruptedException {

        b.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().writeAndFlush(out);
            }
        }).sync();
//                .channel().closeFuture().addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                _l.info(future.toString());
//            }
//        }).sync();

        return (N)this;
    }

    public final N onReceive(final I in) {
        return (N)this;
    }

    public abstract class Receiver<I> {
        abstract void onReceive(final I in);
    }

//    @Override
//    public void close() throws IOException {
//
//    }

    private final static EventLoopGroup _g = new NioEventLoopGroup();
    private final static EventExecutorGroup _e = new DefaultEventExecutorGroup(16);

    private final static Logger _l = LoggerFactory.getLogger(NioHttpClient.class);
}
