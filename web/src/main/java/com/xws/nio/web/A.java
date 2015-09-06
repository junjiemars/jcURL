package com.xws.nio.web;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xws.nio.base.H;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

/**
 * Author:junjie
 * Create:17/5/15.
 * Target:<>
 */
//@SpringBootApplication
public final class A {
    private A() {}

    public static void main(String[] args) {
//        SpringApplication.run(A.class, args);
    }

    public static final String get_post_data(final HttpServletRequest req) {
        final StringBuffer d = new StringBuffer();
        String line = null;
        try {
            final BufferedReader r = req.getReader();
            while ((line = r.readLine()) != null)
                d.append(line);
            return d.toString();
        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }

        return null;
    }

    public static final String http_url() {
        return _http_url;
    }

    public static final int http_bio_timeout() {
        return _http_bio_timeout;
    }

    public static final int http_nio_timeout() {
        return _http_nio_timeout;
    }

    static {
        _http_bio_timeout = H.str_to_int(System.getProperty("http.bio.timeout"), 1000);
        _http_nio_timeout = H.str_to_int(System.getProperty("http.nio.timeout"), 1000);
        _http_url = System.getProperty("http.url");
    }

    private static final int _http_bio_timeout;
    private static final int _http_nio_timeout;
    private static final String _http_url;

    private static final Logger _l = LoggerFactory.getLogger(A.class);
}
