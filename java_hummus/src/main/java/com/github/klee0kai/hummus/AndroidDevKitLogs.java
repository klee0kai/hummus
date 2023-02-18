package com.github.klee0kai.hummus;

import timber.log.Timber;

public class AndroidDevKitLogs {

    public static Timber.Tree devKitLog = Timber.tag("android_devkit");

    static public void w(String text, Object... args) {
        if (devKitLog != null) devKitLog.w(text, args);
    }

    static public void w(Throwable t) {
        if (devKitLog != null) devKitLog.w(t);
    }

}
