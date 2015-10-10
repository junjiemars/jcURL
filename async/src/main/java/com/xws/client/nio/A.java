package com.xws.client.nio;

import com.xws.nio.base.H;

/**
 * Author: junjie
 * Date: 12/31/14.
 * Target: <>
 */
public final class A {
    public static final String NAME = "jcURL";
    public static final String TRY_HELP = String.format("%s: try '%s --help' for more information",
            NAME.toLowerCase(), NAME.toLowerCase());
    public static final String VERSION = "1.0";
    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_VERSION = System.getProperty("os.version");
    //    public static final String LOG4J_CONFIG_PROPERTY = System.getProperty("log4j.configurationFile");
//    public static final String OPTIONS_CONFIG_FILE = "options.json";
    public static final int OPTION_BLOCK_SIZE = 1024;
    public static final int OPTION_NIO_FLAGS;

    static {
        // 0:normal; 1:nio; 2:nio-epoll;
        OPTION_NIO_FLAGS = H.str_to_int(System.getProperty("nio.flags"), 0);
    }

    public static final int OPTION_PROMPT_LEN = 40;
}
