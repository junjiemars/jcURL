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
                new LongOpt("data", LongOpt.REQUIRED_ARGUMENT, null, 'd'),
                new LongOpt("head", LongOpt.NO_ARGUMENT, null, 'I'),
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("request", LongOpt.REQUIRED_ARGUMENT, null, 'X'),
                new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
                new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'V')
        };

        final Getopt g = new Getopt(A.NAME, args, "d:hIX:vV;", opts);
        g.setOpterr(true);
        int c;
        final Options options = new Options();

        while ((c = g.getopt()) != -1)
            switch (c) {
                case 'd':
                    options.data(g.getOptarg());
                    break;
                case 'I':
                    options.has_head(true);
                    break;
                case 'h':
                    help();
                    return;
                case 'X':
                    options.request(g.getOptarg());
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

        process(options);
        JcURL.release();
    }

    private static final void process(final Options o) {
        // do more
        if (Options.H_POST.equals(o.request())) {
            _post(o);
        } else {
            _get(o);
        }
    }

    private static final void _post(final Options o) {
        try {
            final JcURL c = new JcURL()
                    .to(o.url())
                    .post(o.data())
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
                            _l.info("<Received>:\n{}", s);
                        }
                    }, false /* sync */);
        } catch (Exception ex) {
            _l.error(ex.getMessage(), ex);
        }
    }

    private static final void _get(final Options o) {
        try {
            final JcURL c = new JcURL()
                    .to(o.url())
                    .get()
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
                            _l.info("<Received>:\n{}", s);
                        }
                    }, false /* sync */);
        } catch (Exception ex) {
            _l.error(ex.getMessage(), ex);
        }
    }

    private static void help() {
        out.println("Usage: curl [options...] <url>");
        out.println("Options: (H) means HTTP/HTTPS only");
        out.println(" -d, --data DATA     HTTP POST data (H)");
        out.println(" -I, --head          Show document info only");
        out.println(" -h, --help          This help text");
        out.println(" -X, --request COMMAND  Specify request command to use");
        out.println(" -v, --verbose  the operation more talkative");
        out.println(" -V, --version  version number and quit");
    }
}
