package com.github.klee0kai.hummus.threads;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class AndroidThreads {

    public static Looper newThreadLooper(String nameThread) {
        HandlerThread handlerThread = new HandlerThread(nameThread);
        handlerThread.setDaemon(true);
        handlerThread.start();
        return handlerThread.getLooper();
    }

    public static void runMain(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static Handler runMainDelayed(long delay, Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
        return handler;
    }

}
