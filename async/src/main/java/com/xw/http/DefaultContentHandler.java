package com.xw.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: junjie
 * Date: 3/5/2015.
 * Target: <>
 */
public abstract class DefaultContentHandler<T>
        extends SimpleChannelInboundHandler<HttpContent>
        /*implements ReferenceCounted*/ {

    private static final Logger _l = LoggerFactory.getLogger(DefaultContentHandler.class);
    private final ByteBuf _content;
    protected final T _t;

    protected DefaultContentHandler() {
        this(null);
    }

    protected DefaultContentHandler(final T t) {
        this(t, 1024);
    }

    protected DefaultContentHandler(final T t, int capacity) {
        _t = t;
        _content = PooledByteBufAllocator.DEFAULT
//                .heapBuffer(capacity); // faster allocate and access
                .directBuffer(capacity);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        _l.error(cause.getMessage());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpContent content) {
        _content.writeBytes(content.content());

        if (content instanceof LastHttpContent) {
            try {
                process(_content.toString(CharsetUtil.UTF_8));
            } catch (final Exception ex) {
                _l.error(ex.getMessage(), ex);
            } finally {
                ReferenceCountUtil.safeRelease(content.content());
                content.release();
                ctx.close();
                ReferenceCountUtil.safeRelease(_content);
            }
        }
    }

    protected abstract void process(final String s);
}
