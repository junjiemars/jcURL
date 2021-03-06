package com.xws.nio.web;

import com.xws.client.bio.PostRequestBuilder;
import com.xws.client.bio.RequestBuilder;
import com.xws.client.bio.SClient;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        final RequestBuilder<HttpPost> requested = new PostRequestBuilder(A.http_url(), A.get_post_data(req)) {
            @Override
            public void setup(final HttpPost post) {
                final StringEntity entity = new StringEntity(content,
                        ContentType.create("plain/text", Consts.UTF_8));
                entity.setChunked(true);
                post.setEntity(entity);
            }
        };

        final ResponseHandler<String> pipelined = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                final HttpEntity e = response.getEntity();
                if (null == e) {
                    _l.warn("<R:Sync:Entity> is null/empty");
                    return null;
                }

                final String s = EntityUtils.toString(e);

                try {
                    resp.getWriter().write(s);
                    resp.getWriter().flush();
                    resp.getWriter().close();
                } catch (Exception ex) {
                    _l.error(ex.getMessage(), ex);
                }
                return s;
            }
        };

        SClient.request(requested, pipelined);
    }


    private static final Logger _l = LoggerFactory.getLogger(SyncServlet.class);
}
