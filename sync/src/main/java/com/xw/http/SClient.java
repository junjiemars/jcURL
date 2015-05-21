package com.xw.http;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Author: junjie
 * Date: 3/5/2015.
 * Target: <>
 */
public final class SClient {
    private SClient() {
        // forbidden
    }

    public static <T extends HttpUriRequest, R>
    Boolean request(final RequestBuilder<T> requested, final ResponseHandler<R> pipelined) {
        if (null == requested) {
            _l.error("<arg:requested> is invalid");
            return (false);
        }
        if (null == pipelined) {
            _l.error("<arg:pipelined> is invalid");
            return (false);
        }

        final T t = requested.build();
        if (null == t) {
            _l.error("<var:t> is invalid");
            return (false);
        }

        final CloseableHttpClient c = HttpClients.createDefault();
        try {
            c.execute(t, pipelined);
            return (true);
        } catch (final Exception e) {
            _l.error(e);
        } finally {
            try {
                c.close();
            } catch (IOException e) {
                _l.error(e);
            }
        }

        return (false);
    }

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

    private static final Logger _l = LogManager.getLogger(SClient.class);
}


