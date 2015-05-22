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
import java.io.PrintWriter;

/**
 * Author: junjie
 * Date: 4/28/15.
 * Target: <>
 */
@WebServlet(value = "/netty", asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final String uri = System.getProperty("http.url");
        if (H.is_null_or_empty(uri)) {
            Core.output_str(resp, "<R:ENV:http.url> is null/empty");
            return;
        }
        final AsyncContext async = req.startAsync();
        async.setTimeout(H.str_to_int(System.getProperty("echo.timeout"), 1000));

        NClient.request(new PostRequestBuilder(uri, Core.get_post_data(req)) {
            @Override
            public void setup(PostRequestBuilder builder) {

            }
        }, new PipelineBuilder() {
            @Override
            protected void setup(ChannelPipeline pipeline) {
                pipeline.addLast(new DefaultContentHandler<String, AsyncContext>(async) {

                    @Override
                    protected String process(String s) {
                        Core.output_str(_t.getResponse(), s);
                        _t.complete();
                        return s;
                    }
                });
            }
        });
    }

    private static final Logger _l = LogManager.getLogger(AsyncServlet.class);
}
