package com.xw.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: junjie
 * Date: 3/5/2015.
 * Target: <>
 */
public abstract class DefaultContentHandler<T>
        extends SimpleChannelInboundHandler<HttpContent>
        /*implements ReferenceCounted*/ {

    private static final Logger _l = LogManager.getLogger(DefaultContentHandler.class);
    //    private final StringBuilder _content;
    private final ByteBuf _content;

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        _l.info("<READ COMPLETED>");
//    }

    protected DefaultContentHandler() {
        this(1024);
    }

    protected DefaultContentHandler(int capacity) {
        _content = PooledByteBufAllocator.DEFAULT
                .heapBuffer() // faster allocate
                .alloc().buffer(capacity);
//        _content = new StringBuilder(capacity);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        _l.error(cause.getMessage());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpContent content) {
        try {
            _content.writeBytes(content.content());

            if (content instanceof LastHttpContent) {
                try {
                    process(_content.toString(CharsetUtil.UTF_8));
                } catch (final Exception ex) {
                    _l.error(ex);
                } finally {
                    ReferenceCountUtil.safeRelease(_content);
                }
                ctx.close();
            } else {
                content.retain();
            }
        } catch (final Exception ex) {
            _l.error(ex);
        } finally {
            content.release();
        }
    }

    protected abstract T process(final String s);
}
