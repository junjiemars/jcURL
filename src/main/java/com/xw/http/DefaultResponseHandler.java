package com.xw.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: junjie
 * Date: 3/13/15.
 */
public final class DefaultResponseHandler extends SimpleChannelInboundHandler<HttpResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpResponse msg) throws Exception {
        _l.info("STATUS: " + msg.status());
        _l.info("VERSION: " + msg.protocolVersion());

        if (!msg.headers().isEmpty()) {
            for (CharSequence name : msg.headers().names()) {
                for (CharSequence value : msg.headers().getAll(name)) {
                    _l.info("HEADER: " + name + " = " + value);
                }
            }
        }

        if (HttpHeaderUtil.isTransferEncodingChunked(msg)) {
            _l.info("CHUNKED CONTENT {");
        } else {
            _l.info("CONTENT {");
        }
        _l.info(msg);
    }

    private static final Logger _l = LogManager.getLogger(DefaultResponseHandler.class);
}
