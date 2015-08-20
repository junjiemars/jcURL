package com.xw.http.web;

import com.xw.http.H;
import com.xw.http.NioHttpClient;
import com.xw.http.Receiver;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final AsyncContext async = req.startAsync(req, resp);
        async.setTimeout(C.http_nio_timeout());

        final String uri = C.http_url();
        if (H.is_null_or_empty(uri)) {
            _l.info("#%s:<ENV:http.url> is null/empty", AsyncNioServlet.class.getSimpleName());
            return;
        }

        try {
            final NioHttpClient n = new NioHttpClient()
                    .to(uri)
                    .post(C.get_post_data(req))
                    .onReceive(new Receiver(false) {
                        @Override
                        public void onReceive(String s) {
//                            _l.debug(s);
                            try {

                                async.getResponse().getWriter().write(s);
                                async.getResponse().getWriter().flush();

                            } catch (IOException ioe) {
                                _l.error(ioe.getMessage(), ioe);
                            }
                        }

                        @Override
                        public void onDone() {
                            _l.debug("#completed nio call");
                            async.complete();
                        }
                    });


        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }

    }
    private static final Logger _l = LoggerFactory.getLogger(AsyncNioPureServlet.class);
}
