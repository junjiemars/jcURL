package com.xw.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: junjie
 * Date: 3/13/15.
 */
public final class DefaultResponseHandler extends SimpleChannelInboundHandler<HttpResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpResponse response) throws Exception {
        _l.info(String.format("<Tid:%s>", H.tid()));
        _l.info("STATUS: " + response.status());
        _l.info("VERSION: " + response.protocolVersion());

        if (!response.headers().isEmpty()) {
            for (CharSequence name : response.headers().names()) {
                for (CharSequence value : response.headers().getAll(name)) {
                    _l.info("HEADER: " + name + " = " + value);
                }
            }
        }

//        if (HttpHeaderUtil.isTransferEncodingChunked(response)) {
//            _l.info("CHUNKED CONTENT {");
//        } else {
//            _l.info("CONTENT {");
//        }
//        _l.info(response);
    }

    private static final Logger _l = LogManager.getLogger(DefaultResponseHandler.class);
}
