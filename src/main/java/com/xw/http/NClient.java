package com.xw.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Author: junjie
 * Date: 3/5/2015.
 * Target: <>
 */
public final class NClient {
    private NClient() {
        // forbidden
    }

    public static boolean post(final RequestBuilder requested, final PipelineBuilder pipelined) {
        if (!_check_args(requested, pipelined)) return (false);

        return (request(requested.uri(), requested.build_post(), pipelined));
    }

    public static boolean get(final RequestBuilder requested, final PipelineBuilder pipelined) {
        if (!_check_args(requested, pipelined)) return (false);

        return (request(requested.uri(), requested.build_get(), pipelined));
    }

    private static boolean _check_args(final RequestBuilder requested,
                                       final PipelineBuilder pipelined) {
        if (null == requested) {
            _l.error("<arg:requested> is invalid");
            return (false);
        }
        if (null == pipelined) {
            _l.error("<arg:pipelined> is invalid");
            return (false);
        }

        return (true);
    }

    public static <T extends DefaultFullHttpRequest> boolean request(
            final RequestBuilder0<T> requested,
            final PipelineBuilder pipelined) {

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

        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap()
                    .remoteAddress(uri.getHost(), (-1 == uri.getPort() ? 80 : uri.getPort()))
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            pipelined.build(ch.pipeline());
                        }
                    });

            final Channel c = b.connect().sync().channel();
            c.writeAndFlush(requested);
            c.closeFuture().sync();

            return (true);
        } catch (final Exception e) {
            _l.error(e);
        } finally {
            group.shutdownGracefully();
        }

        return (false);
    }

    private static boolean request(final URI uri,
                                   final FullHttpRequest requested,
                                   final PipelineBuilder pipelined) {
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap()
                    .remoteAddress(uri.getHost(), (-1 == uri.getPort() ? 80 : uri.getPort()))
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            pipelined.build(ch.pipeline());
                        }
                    });

            final Channel c = b.connect().sync().channel();
            c.writeAndFlush(requested);
            c.closeFuture().sync();

            return (true);
        } catch (final Exception e) {
            _l.error(e);
        } finally {
            group.shutdownGracefully();
        }

        return (false);
    }

    private static URI _to_uri(final String url) {
        if (H.is_null_or_empty(url)) {
            _l.warn("<arg:url> is invalid");
            return (null);
        }

        try {
            return (new URI(url));
        } catch (final URISyntaxException e) {
            _l.error(e);
        }

        return (null);
    }


    private static final Logger _l = LogManager.getLogger(NClient.class);
}


