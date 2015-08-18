package com.xw.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * Author: junjie
 * Date: 3/5/2015.
 * Target: <>
 */
public final class NClient  {
    private NClient() {
        // forbidden
    }

    public static <T extends DefaultFullHttpRequest>
    boolean request(final RequestBuilder<T> requested, final PipelineBuilder pipelined) {

        if (null == requested) {
            _l.error("<arg:requested> is invalid");
            return (false);
        }
        if (null == pipelined) {
            _l.error("<arg:pipelined> is invalid");
            return (false);
        }

        final T t = requested.build();
        final URI uri = _to_uri(t.uri());
        if (null == uri) {
            _l.error("<var:uri> is invalid");
            return (false);
        }

        t.headers().set(HttpHeaderNames.HOST, uri.getHost());

//        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap()
                    .remoteAddress(uri.getHost(), (-1 == uri.getPort() ? 80 : uri.getPort()))
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .group(_g)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            pipelined.build(ch.pipeline());
                        }
                    });

            final ChannelFuture f0 = b.connect();
            f0.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    final Channel c = future.channel();
                    c.writeAndFlush(t);
                    c.closeFuture().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            _l.info("#nested completed event");
                        }
                    });

                }
            });

//            final Channel c = b.connect().sync().channel();
//            c.writeAndFlush(t);
//            c.closeFuture().sync();

            return (true);
        } catch (final Exception e) {
            _l.error(e.getMessage(), e);
        } finally {
//            group.shutdownGracefully();
        }

        return (false);
    }

    private static URI _to_uri(final String url) {
        if (H.is_null_or_empty(url)) {
            _l.warn("#arg:url is invalid");
            return (null);
        }

        try {
            return (new URI(url));
        } catch (final URISyntaxException e) {
            _l.error(e.getMessage(), e);
        }

        return (null);
    }

    private static final EventLoopGroup _g = new NioEventLoopGroup();
    private static final Logger _l = LoggerFactory.getLogger(NClient.class);
}


