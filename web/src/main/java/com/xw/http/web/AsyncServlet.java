package com.xw.http.web;

import com.xw.http.*;
import io.netty.channel.ChannelPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final AsyncContext async = req.startAsync();
        async.setTimeout(C.http_timeout());

        final String uri = C.http_url();
        if (H.is_null_or_empty(uri)) {
            C.output_str(async.getResponse(), "<R:ENV:http.url> is null/empty");
            return;
        }

        NClient.request(new PostRequestBuilder(uri, C.get_post_data(req)) {
            @Override
            public void setup(PostRequestBuilder builder) {

            }
        }, new PipelineBuilder() {
            @Override
            protected void setup(ChannelPipeline pipeline) {
                pipeline.addLast(new DefaultContentHandler<String, AsyncContext>(async) {
                    @Override
                    protected String process(String s) {
                        C.output_str(async.getResponse(), s);
                        async.complete();
                        return s;
                    }
                });
            }
        });
    }

    private static final Logger _l = LogManager.getLogger(AsyncServlet.class);
}
