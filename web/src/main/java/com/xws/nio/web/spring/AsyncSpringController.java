package com.xws.nio.web.spring;


import com.xws.client.nio.NioHttpClient;
import com.xws.client.nio.Receiver;
import com.xws.nio.web.A;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by junjie on 8/31/15.
 */
@Controller
public class AsyncSpringController {

    @RequestMapping(value = "pure", method = RequestMethod.POST,
            headers = "Accept=*/*",
            produces = "text/plain")
    public
    @ResponseBody
    DeferredResult<String> pure() {

        final DeferredResult<String> result = new DeferredResult<String>(A.http_nio_timeout());

        try {
            final NioHttpClient n = new NioHttpClient()
                    .to(A.http_url())
                    .get()
                    .headers(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                    .headers(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
//                            _l.debug(s);
                            try {
                                result.setResult(s);
                            } catch (Exception e) {
                                _l.error(e.getMessage(), e);
                            } finally {
                                _l.info("#DeferredResult<String> completed");
                            }
                        }
                    });

        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }

        return result;
    }


    private static final Logger _l = LoggerFactory.getLogger(AsyncSpringController.class);

}
