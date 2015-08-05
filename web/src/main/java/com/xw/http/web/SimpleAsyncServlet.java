package com.xw.http.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

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
        final AsyncContext ctx = req.startAsync();

        // set the timeout
        ctx.setTimeout(30000);

        // attach listener to respond to lifecycle events of this AsyncContext
        ctx.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                _l.info("#onComplete called");
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                _l.info("#onTimeout called");
            }

            public void onError(AsyncEvent event) throws IOException {
                _l.info("#onError called");
            }

            public void onStartAsync(AsyncEvent event) throws IOException {
                _l.info("#onStartAsync called");
            }
        });

        // spawn some task in a background thread
        ctx.start(new Runnable() {
            @Override
            public void run() {
                try {
                    final PrintWriter o = ctx.getResponse().getWriter();
                    Thread.sleep(2000);
                    o.write(MessageFormat.format("<h1>Processing task in bgt_id:[{0}] at {1}</h1>\n",
                            Thread.currentThread().getId(),
                            System.currentTimeMillis()));
                    Thread.sleep(1000);
                    o.write(MessageFormat.format("<h1>another processing at {0}\n",
                            System.currentTimeMillis()));
                    o.write(MessageFormat.format("<h1>Processing task in bgt_id:[{0}]</h1>\n",
                            Thread.currentThread().getId()));

                    ctx.complete();
                } catch (IOException e) {
                    _l.error("#Problem processing task", e);
                } catch (InterruptedException ie) {
                    _l.error("#Problem processing task", ie);
                }
            }
        });

    }

    private static final Logger _l = LogManager.getLogger(SimpleAsyncServlet.class);
}
