package com.logicanvas.boardgames.ludo.utility;

import com.logicanvas.boardgames.ludo.config.GameConfiguration;

/**
 * Created by amansoor on 24-08-2015.
 */
public class LudoLogger {
    public static void log(String logStr) {
        System.out.println(logStr);
    }

    public static void info(String logStr) {
        System.out.println("[INFO]: " + logStr);
    }

    public static void warn(String logStr) {
        System.out.println("[WARN]: " + logStr);
    }

    public static void error(String logStr) {
        System.out.println("[ERROR]: " + logStr);
    }

    public static void debug(String logStr) {
        if (GameConfiguration.DEBUG) {
            System.out.println("[DEBUG]: " + logStr);
        }
    }

    public static void debug(Object obj) {
        if (GameConfiguration.DEBUG) {
            System.out.println("[DEBUG]: ");
            System.out.println(obj);
        }
    }
}
