package com.xw.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * Author: junjie
 * Date: 4/2/15.
 * Target: <>
 */
public abstract class PostRequestBuilder
        extends DefaultFullHttpRequest
        implements RequestBuilder<PostRequestBuilder> {

    public PostRequestBuilder(final String uri, final String content) {
        this(HttpVersion.HTTP_1_1, HttpMethod.POST,
                uri,
                H.is_null_or_empty(content) ? null :
                        PooledByteBufAllocator.DEFAULT
                                .directBuffer()
                                .writeBytes(content.getBytes(CharsetUtil.UTF_8)),
                false);
    }

    protected PostRequestBuilder(HttpVersion httpVersion, HttpMethod method,
                                 String uri, ByteBuf content, boolean validateHeaders) {
        super(httpVersion, method, uri, content, validateHeaders);
    }

    @Override
    public final PostRequestBuilder build() {
        headers()
//                .set(HttpHeaderNames.HOST, _uri.getHost())
                .set(HttpHeaderNames.ACCEPT_CHARSET, CharsetUtil.UTF_8)
                .set(HttpHeaderNames.USER_AGENT, USER_AGENT)
                .set(HttpHeaderNames.ACCEPT, ACCEPT_ALL)
                .set(HttpHeaderNames.CONTENT_LENGTH, content().readableBytes())
        ;
        setup(this);

        return (this);
    }
}

