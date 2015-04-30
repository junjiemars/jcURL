package com.xw.http.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Author: junjie
 * Date: 4/28/15.
 * Target: <>
 */
@WebServlet(value = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
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
            public void run() {
                try {
                    Thread.sleep(2000);
                    ctx.getResponse().getOutputStream().println(
                            MessageFormat.format("<h1>Processing task in bgt_id:[{0}]</h1>\n",
                            Thread.currentThread().getId()));
//                    ctx.getResponse().getWriter().write(
//                            MessageFormat.format("<h1>Processing task in bgt_id:[{0}]</h1>\n",
//                                    Thread.currentThread().getId()));
                } catch (IOException e) {
                    _l.error("#Problem processing task", e);
                } catch (InterruptedException ie) {
                    _l.error("#Problem processing task", ie);
                }


                ctx.complete();
            }
        });

    }

    private static final Logger _l = LogManager.getLogger(AsyncServlet.class);
}
