package com.xw.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by junjie on 3/5/2015.
 */
public class DefaultContentHandler extends SimpleChannelInboundHandler<HttpContent> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        _l.error(cause.getMessage());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpContent content) {
        _l.info(String.format("<Tid:%s>", H.tid()));
        _l.info(content.content().toString(CharsetUtil.UTF_8));

        if (content instanceof LastHttpContent) {
            _l.info("} END OF CONTENT");
            ctx.close();
        }
    }

    private static final Logger _l = LogManager.getLogger(DefaultContentHandler.class);
}
