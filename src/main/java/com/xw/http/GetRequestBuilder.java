package com.xw.http;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Author: junjie
 * Date: 4/2/15.
 * Target: <>
 */
public abstract class GetRequestBuilder
        extends DefaultFullHttpRequest
        implements RequestBuilder<GetRequestBuilder> {

    public GetRequestBuilder(final String url) {
        this(HttpVersion.HTTP_1_1, HttpMethod.GET, url, true);
    }

    protected GetRequestBuilder(HttpVersion httpVersion, HttpMethod method,
                                String uri, boolean validateHeaders) {
        super(httpVersion, method, uri, validateHeaders);
    }

    @Override
    public final GetRequestBuilder build() {
        setup(this);

        return (this);
    }
}
