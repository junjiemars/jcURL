package com.xws.nio.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: junjie
 * Date: 3/13/15.
 */
public abstract class DefaultHeaderHandler<T> extends SimpleChannelInboundHandler<HttpResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpResponse response) throws Exception {
        // customized processing...
        process(response);
    }

    protected abstract T process(final HttpResponse response);

    private static final Logger _l = LoggerFactory.getLogger(DefaultHeaderHandler.class);
}
