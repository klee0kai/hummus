package com.github.klee0kai.hummus.threads;

import com.github.klee0kai.hummus.AndroidDevKitLogs;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FutureHolder<T> {

    private Future<T> future = null;

    public Future<T> set(Future<T> future) {
        return this.future = future;
    }

    public Future<T> get() {
        return future;
    }

    public boolean isInProcess() {
        return future != null && !future.isDone() && !future.isCancelled();
    }

    public void cancel() {
        if (future != null && !future.isDone())
            future.cancel(false);
    }


    public T tryAwait() {
        try {
            return future != null ? future.get() : null;
        } catch (Exception e) {
            AndroidDevKitLogs.w(e);
            return null;
        }
    }

    public T getResult() {
        try {
            return future != null && future.isDone() ? future.get(1, TimeUnit.MICROSECONDS) : null;
        } catch (Exception e) {
            AndroidDevKitLogs.w(e);
            return null;
        }
    }

    public T popResult() {
        try {
            T res = future != null && future.isDone() ? future.get(1, TimeUnit.MICROSECONDS) : null;
            if (res != null)
                future = null;
            return res;
        } catch (Exception e) {
            AndroidDevKitLogs.w(e);
            return null;
        }
    }


}
