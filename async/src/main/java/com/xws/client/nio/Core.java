package com.xws.client.nio;

import com.xws.nio.base.H;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.System.out;

/**
 * Author: junjie
 * Date: 3/12/15.
 */
public final class Core {
    private static final Logger _l = LoggerFactory.getLogger(Core.class);

    private Core() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            out.println(A.TRY_HELP);
            return;
        }

        final LongOpt[] opts = new LongOpt[]{
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
                new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'V'),
                new LongOpt("head", LongOpt.NO_ARGUMENT, null, 'I')
        };

        final Getopt g = new Getopt(A.NAME, args, "hvVI;", opts);
        g.setOpterr(true);
        int c;
        final Options options = new Options();

        while ((c = g.getopt()) != -1)
            switch (c) {
                case 'I':
                    options.has_head(true);
                    break;
                case 'v':
                    // verbose
                    options.is_verbose(true);
                    break;
                case 'V':
                    // version
                    out.println(String.format("%s %s (%s %s)", A.NAME, A.VERSION, A.OS_NAME, A.OS_VERSION));
                    out.println("Protocols: http");
                    out.println("Features: ");
                    return;
                case 'h':
                    help();
                    return;
                case ':':
                    out.println(A.TRY_HELP);
                case '?':
                    out.println(A.TRY_HELP);
                    return;
                default:
                    options.unknown_opts(c);
                    break;
            }

        for (int i = g.getOptind(); i < args.length ; i++) {
            options.url(args[i]);
        }
        if (0 == options.urls().size()) {
            out.println(String.format("%s: no URL specified!", A.NAME));
        }

        get(options);
    }

//    private static final void post(final Options o) {
//        try {
//            final JcURL c = new JcURL()
//                    .to(o.url())
//                    .post(o.data())
//                    .onReceive(new Receiver<String>() {
//                        @Override
//                        public void onReceive(final String s) {
//                            _l.info("<Received>:\n{}", s);
//                        }
//                    }, false /* sync */);
//        } catch (Exception ex) {
//            _l.error(ex.getMessage(), ex);
//        }
//    }

    private static final void get(final Options o) {
        try {
            final JcURL c = new JcURL()
                    .to(o.url())
                    .get()
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
                            _l.info("<Received>:\n{}", s);
                        }
                    });
        } catch (Exception ex) {
            _l.error(ex.getMessage(), ex);
        }
    }

    private static void help() {
        out.println("Usage: curl [options...] <url>");
        out.println("Options: (H) means HTTP/HTTPS only");
        out.println(" -v, --verbose \t the operation more talkative");
        out.println(" -V, --version \t version number and quit");
    }

    private static final AtomicInteger _sn = new AtomicInteger();
}
