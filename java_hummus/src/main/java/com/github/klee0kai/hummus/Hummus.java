package com.github.klee0kai.hummus;

import com.github.klee0kai.hummus.logger.IHummusLogger;

public class Hummus {

    public static IHummusLogger hummusLog = null;

    public static boolean isDebug = false;

    static public void w(String text, Object... args) {
        if (hummusLog != null) hummusLog.w(text, args);
    }

    static public void w(Throwable t) {
        if (hummusLog != null) hummusLog.w(t);
    }

}
