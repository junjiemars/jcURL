package com.xw.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: junjie
 * Date: 3/5/2015.
 * Target: <>
 */
public abstract class DefaultContentHandler<T> extends SimpleChannelInboundHandler<HttpContent> {

    protected DefaultContentHandler() {
        this(512);
    }

    protected DefaultContentHandler(int capacity) {
        _buf = new StringBuilder(capacity);
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        _l.info("<READ COMPLETED>");
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        _l.error(cause.getMessage());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpContent content) {
        _buf.append(content.content().toString(CharsetUtil.UTF_8));

        if (content instanceof LastHttpContent) {
            process(_buf.toString());
            content.release();
            ctx.close();
        }
    }

    protected abstract T process(final String s);

    private final StringBuilder _buf;

    private static final Logger _l = LogManager.getLogger(DefaultContentHandler.class);

}