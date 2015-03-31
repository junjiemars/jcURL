package com.xw.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executor;

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
        _buf = PooledByteBufAllocator.DEFAULT
                .heapBuffer()
                .alloc().buffer(capacity);
//        _buf = new StringBuilder(capacity);
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
//        _buf.append(content.content().toString(CharsetUtil.UTF_8));
        _buf.writeBytes(content.content());
        if (content instanceof LastHttpContent) {
            try {
                process(_buf.toString(CharsetUtil.UTF_8));
            } catch (final Exception ex) {
                _l.error(ex);
            } finally {
                content.release();
                ReferenceCountUtil.safeRelease(_buf);
            }

            ctx.close();
        }
    }

    protected abstract T process(final String s);

//    private final StringBuilder _buf;
    private final ByteBuf _buf;

    private static final Logger _l = LogManager.getLogger(DefaultContentHandler.class);

}
