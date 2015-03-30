package com.xw.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

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

    private static final Logger _l = LogManager.getLogger(NClient.class);
}


