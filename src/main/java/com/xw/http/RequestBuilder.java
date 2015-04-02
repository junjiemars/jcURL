package com.xw.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Author: junjie
 * Date: 3/12/15.
 */
public abstract class RequestBuilder {
    protected RequestBuilder(final String url) {
        this(url, null);
    }

//    protected RequestBuilder(final String url, final String data) {
//        this(url, data, 0);
//    }

    protected RequestBuilder(final String url, final String data/*, final int timeout*/) {
        _uri = _to_uri(url);
        _buf = H.is_null_or_empty(data) ? null : _to_buf(data);
    }

    public final URI uri() {
        return (_uri);
    }

    private final URI _uri;
    private final ByteBuf _buf;
//    private final Integer _timeout;

    public final FullHttpRequest build_post() {
        if (null == _uri) {
            _l.error("<var:_uri> is invalid");
            return (null);
        }
        if (null == _buf) {
            _l.error("<var:_buf> is invalid");
            return (null);
        }

        final FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                _uri.getRawPath(),
                _buf.duplicate());

        request.headers().set(HttpHeaderNames.HOST, _uri.getHost())
                .set(HttpHeaderNames.ACCEPT_CHARSET, CharsetUtil.UTF_8)
                .set(HttpHeaderNames.USER_AGENT, USER_AGENT)
                .set(HttpHeaderNames.ACCEPT, ACCEPT_ALL)
                .set(HttpHeaderNames.CONTENT_LENGTH, _buf.readableBytes())
                ;

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

    protected abstract FullHttpRequest setup(final FullHttpRequest request);

    protected static final AsciiString TEXT_XML = new AsciiString("text/xml;charset=utf-8");

    protected static final AsciiString USER_AGENT = new AsciiString(A.NAME);

    protected static final AsciiString ACCEPT_ALL = new AsciiString("*/*");

//    protected static final AsciiString EXPECT_100 = new AsciiString("100-continue");

    private static URI _to_uri(final String url) {
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

    private static ByteBuf _to_buf(final String data) {
        return (PooledByteBufAllocator.DEFAULT
//                .heapBuffer() // JVM heap buffer
                .directBuffer(512) // out side of the JVM heap buffer
                .writeBytes(data.getBytes(CharsetUtil.UTF_8)));
    }

    private static final Logger _l = LogManager.getLogger(RequestBuilder.class);
}
