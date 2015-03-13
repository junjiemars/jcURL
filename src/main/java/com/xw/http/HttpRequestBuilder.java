package com.xw.http;

import io.netty.handler.codec.http.*;

/**
 * Author: junjie
 * Date: 3/12/15.
 */
public abstract class HttpRequestBuilder {



    public abstract HttpRequest setup(final HttpRequest request);
}
