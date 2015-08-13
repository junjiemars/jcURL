package com.xw.http.web;

import com.xw.http.H;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Random;

/**
 * Author: junjie
 * Date: 4/28/15.
 * Target: <>
 */
//@WebServlet(urlPatterns = {"/simple"}, asyncSupported = true)
public class SimpleAsyncServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);

        // create the async context, otherwise getAsyncContext() will be null
        final AsyncContext ctx = req.startAsync(req, resp);

        // set the timeout
        ctx.setTimeout(C.http_nio_timeout());

        _l.info("#Has original Request/Response object:{}", ctx.hasOriginalRequestAndResponse());

        // attach listener to respond to lifecycle events of this AsyncContext
        ctx.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                _l.info("#AsyncListener:onComplete called");
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                _l.info("#AsyncListener:onTimeout called");
            }

            public void onError(AsyncEvent event) throws IOException {
                _l.info("#AsyncListener:onError called");
            }

            public void onStartAsync(AsyncEvent event) throws IOException {
                _l.info("#AsyncListener:onStartAsync called");
            }
        });

        // spawn some task in a background thread
        ctx.start(new Runnable() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    Thread.sleep(new Random().nextInt(2000));
                    ctx.getResponse().getWriter().printf("Thread %s completed the task in %d ms.\n",
                            H.tn(), H.tid());
                } catch (Exception e) {
                    _l.error(e.getMessage(), e);
                } finally {
                    ctx.complete();
                }
            }
        });

    }

    private static final Logger _l = LoggerFactory.getLogger(SimpleAsyncServlet.class);
}
