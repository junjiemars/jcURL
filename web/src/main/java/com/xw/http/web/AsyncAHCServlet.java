package com.xw.http.web;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.xw.http.H;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.concurrent.Future;

/**
 * Author: junjie
 * Date: 8/11/15.
 * Target: <>
 */
public class AsyncAHCServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);

        //        super.doPost(req, resp);
        final AsyncContext ctx = req.startAsync(req, resp);
        ctx.setTimeout(C.http_nio_timeout());

        final String uri = C.http_url();
        if (H.is_null_or_empty(uri)) {
            _l.warn("#Must specify the http.url");
            ctx.complete();
            return;
        }

        final AsyncHttpClient ahc = new AsyncHttpClient();
        ctx.start(new Runnable() {
            @Override
            public void run() {
                final Future<Response> f = ahc.prepareGet(uri).execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) throws Exception {
                        return response;
                    }
                });

                try {
                    ctx.getResponse().getWriter().write(f.get().getResponseBody().toString());
                    ctx.getResponse().getWriter().write("\n");
                } catch (Exception e) {
                    _l.error(e.getMessage(), e);
                } finally {
                    ctx.complete();
                }
            }
        });

    }

    private static final Logger _l = LoggerFactory.getLogger(AsyncAHCServlet.class);
}
