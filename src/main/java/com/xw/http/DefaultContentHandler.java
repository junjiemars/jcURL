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
    private final ByteBuf _content;

    protected DefaultContentHandler() {
        this(1024);
    }

    protected DefaultContentHandler(int capacity) {
        _content = PooledByteBufAllocator.DEFAULT
                .heapBuffer(capacity); // faster allocate and access
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
                    final String s = _content.toString(CharsetUtil.UTF_8);
                    ctx.executor().execute(new Runnable() {
                        @Override
                        public void run() {
                            process(s);
                        }
                    });
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

    private static final void nio_process() {

    }

    protected abstract T process(final String s);
}
