package com.xw.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Author: junjie
 * Date: 3/12/15.
 */
public abstract class RequestBuilder {
    public RequestBuilder(final String url) {
        this(url, null);
    }

    public RequestBuilder(final String url, final String content) {
        _uri = _to_uri(url);
        _content = (H.is_null_or_empty(content)
                ? null : Unpooled.copiedBuffer(content.getBytes(CharsetUtil.UTF_8)));
    }

    public final URI uri() {
        return (_uri);
    }

    private final URI _uri;
    private final ByteBuf _content;

    public final FullHttpRequest build_post() {
        if (null == _uri) {
            _l.error("<var:_uri> is invalid");
            return (null);
        }
        if (null == _content) {
            _l.error("<var:_content> is invalid");
            return (null);
        }

        final FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                _uri.getRawPath(),
                _content);

        request.headers().set(HttpHeaderNames.HOST, _uri.getHost());

        return (setup(request));
    }

    public final FullHttpRequest build_get() {
        if (null == _uri) {
            _l.error("<var:_uri> is invalid");
            return (null);
        }

        final FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                _uri.getRawPath());

        request.headers().set(HttpHeaderNames.HOST, _uri.getHost());

        return (setup(request));
    }

    public abstract FullHttpRequest setup(final FullHttpRequest request);

    private static final URI _to_uri(final String url) {
        if (H.is_null_or_empty(url)) {
            _l.warn("<arg:url> is invalid");
            return (null);
        }

        try {
            return (new URI(url));
        } catch (final URISyntaxException e) {
            _l.error(e);
        }

        return (null);
    }

    private static final Logger _l = LogManager.getLogger(RequestBuilder.class);
}
