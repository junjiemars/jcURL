package com.xw.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by junjie on 3/5/2015.
 */
public final class NClient {
    public static boolean post(final String url, final HttpRequestBuilder builder) {
        final URI uri = check(url, builder);
        if (null == uri) {
            return (false);
        }

        final HttpRequest p = builder.setup(_build(uri, HttpMethod.POST));
        return (request(uri, p));
    }

    public static boolean get(final String url, final HttpRequestBuilder builder) {
        final URI uri = check(url, builder);
        if (null == uri) {
            return (false);
        }

        final HttpRequest p = builder.setup(_build(uri, HttpMethod.GET));
        return (request(uri, p));
    }

    private static final URI check(final String url, final HttpRequestBuilder builder) {
        if (H.is_null_or_empty(url)) {
            _l.warn("arg:url is null/empty");
            return (null);
        }
        if (null == builder) {
            _l.warn("arg:builder is null");
            return (null);
        }

        final URI uri = _to_uri(url);
        if (null == uri) {
            _l.warn(String.format("invalid URI<%s>", url));
        }
        return (uri);
    }

    private static boolean request(final URI uri, final HttpRequest request) {
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NClientInitializer());

            final ChannelFuture f = b.connect(uri.getHost(), (-1 == uri.getPort() ? 80 : uri.getPort()));
            final Channel c = f.sync().channel();

            c.writeAndFlush(request);
            c.closeFuture().sync();
            return (true);
        } catch (final Exception e) {
            _l.error(e);
        } finally {
            group.shutdownGracefully();
        }

        return (false);
    }

    private static final URI _to_uri(final String url) {
        if (H.is_null_or_empty(url)) {
            return (null);
        }

        try {
            return (new URI(url));
        } catch (final URISyntaxException e) {
            _l.error(e);
        }

        return (null);
    }

    private static final HttpRequest _build(final URI uri, final HttpMethod op) {
        final HttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, op, uri.getRawPath());
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        return (request);
    }

    private static final Logger _l = LogManager.getLogger(NClient.class);
}


