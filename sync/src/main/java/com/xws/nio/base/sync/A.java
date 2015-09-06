package com.xws.nio.base.sync;

import com.xws.nio.base.H;

/**
 * Author: junjie
 * Date: 12/31/14.
 * Target: <>
 */
public final class A {
    public static final String NAME = "sync";
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
