package com.github.klee0kai.hummus;

import com.github.klee0kai.hummus.logger.IHummusLogger;

public class Hummus {

    public static IHummusLogger hummusLogger = null;

    public static boolean isDebug = false;

    static public void w(String text, Object... args) {
        if (hummusLogger != null) hummusLogger.w(text, args);
    }

    static public void w(Throwable t) {
        if (hummusLogger != null) hummusLogger.w(t);
    }

}
