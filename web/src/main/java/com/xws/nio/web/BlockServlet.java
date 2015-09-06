package com.xws.nio.web;

import com.xws.nio.base.H;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

/**
 * Author: junjie
 * Date: 8/11/15.
 * Target: <>
 */
public class BlockServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(new Random().nextInt(A.http_bio_timeout() / 3));
            resp.getWriter().printf("Thread %s completed the task in %d ms.\n",
                    H.tn(), H.tid());

        } catch (Exception ex) {
            _l.error(ex.getMessage(), ex);
        }
    }

    private static final Logger _l = LoggerFactory.getLogger(BlockServlet.class);
}
