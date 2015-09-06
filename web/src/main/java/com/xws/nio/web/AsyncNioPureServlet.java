package com.xws.nio.web;

import com.xws.nio.base.H;
import com.xws.nio.client.NioHttpClient;
import com.xws.nio.client.Receiver;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by junjie on 8/18/15.
 */
public class AsyncNioPureServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);

        final AsyncContext ctx = req.startAsync(req, resp);
        ctx.setTimeout(A.http_nio_timeout());

        final String uri = A.http_url();
        if (H.is_null_or_empty(uri)) {
            _l.info("#%s:<ENV:http.url> is null/empty", AsyncNioServlet.class.getSimpleName());
            return;
        }

        try {
            final NioHttpClient n = new NioHttpClient()
                    .to(uri)
                    .get()
                    .headers(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                    // override previous header setting
                    .headers(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
//                            _l.debug(s);
                            try {
                                ctx.getResponse().getWriter().write(s);
                            } catch (Exception e) {
                                _l.error(e.getMessage(), e);
                            } finally {
                                ctx.complete();
                            }
                        }
                    });

        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);

        final AsyncContext ctx = req.startAsync(req, resp);
        ctx.setTimeout(A.http_nio_timeout());

        final String uri = A.http_url();
        if (H.is_null_or_empty(uri)) {
            _l.info("#%s:<ENV:http.url> is null/empty", AsyncNioServlet.class.getSimpleName());
            return;
        }

        try {
            final NioHttpClient n = new NioHttpClient()
                    // set the target uri
                    .to(uri)

                    // set the posting data
                    .post(A.get_post_data(req))

                    // Here: you can set your client cookies if you needed it.
//                    .headers(HttpHeaderNames.COOKIE,
//                            ClientCookieEncoder.STRICT.encode(
//                                    new DefaultCookie("cookie0", "value0"),
//                                    new DefaultCookie("cookie1", "value1")))

                    // how to receive the reponse data is your job
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
//                            _l.debug(s);
                            try {
                                ctx.getResponse().getWriter().write(s);
                            } catch (Exception e) {
                                _l.error(e.getMessage(), e);
                            } finally {
                                ctx.complete();
                            }
                        }
                    });

        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }

    }

    private static final Logger _l = LoggerFactory.getLogger(AsyncNioPureServlet.class);
}
