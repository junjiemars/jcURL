package com.xws.nio.base.sync;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

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

    private static final Logger _l = LogManager.getLogger(SClient.class);
}


