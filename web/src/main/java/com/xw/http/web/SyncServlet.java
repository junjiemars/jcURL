package com.xw.http.web;

import com.xw.http.H;
import com.xw.http.sync.PostRequestBuilder;
import com.xw.http.sync.RequestBuilder;
import com.xw.http.sync.SClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author: junjie
 * Date: 4/20/15.
 * Target: <>
 */
//@WebServlet("/HelloWorld")
public class SyncServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final String uri = System.getProperty("http.url");
        if (H.is_null_or_empty(uri)) {
            resp.getOutputStream().println("<R:ENV:http.url> is null/empty");
            resp.getOutputStream().close();
            return;
        }

        final RequestBuilder<HttpPost> requested = new PostRequestBuilder(uri, "Hello") {
            @Override
            public void setup(final HttpPost post) {

            }
        };

        final ResponseHandler<String> pipelined = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                final HttpEntity e = response.getEntity();
                if (null == e) {
                    resp.getOutputStream().println("<R:Sync:Entity> is null/empty");
                    return null;
                }

                final String s = EntityUtils.toString(e);
                final PrintWriter w = resp.getWriter();
                w.write(s);
                w.flush();
                w.close();
                return s;
            }
        };

        SClient.request(requested, pipelined);
    }


    private static final Logger _l = LogManager.getLogger(SyncServlet.class);
}
