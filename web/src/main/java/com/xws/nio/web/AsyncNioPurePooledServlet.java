package com.xws.nio.web;

import com.xws.client.nio.JcURL;
import com.xws.client.nio.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by junjie on 8/25/15.
 */
public final class AsyncNioPurePooledServlet extends HttpServlet {

    private ExecutorService executor;

    @Override
    public void init() throws ServletException {
        super.init();
        executor = Executors.newWorkStealingPool();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);

        final AsyncContext ctx = req.startAsync(req, resp);
        ctx.setTimeout(A.http_nio_timeout());

        try {
            final JcURL n = new JcURL()
                    .to(A.http_url())
                    .post(A.get_post_data(req))
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
//                            _l.debug(s);

                            // run it with Executor for long running task
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ctx.getResponse().getWriter().write(s);
                                    } catch (Exception e) {
                                        _l.error(e.getMessage(), e);
                                    } finally {
                                        ctx.complete();
                                    }
                                }
                            });
                        }
                    });


        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }

    }

    private static final Logger _l = LoggerFactory.getLogger(AsyncNioPurePooledServlet.class);
}
