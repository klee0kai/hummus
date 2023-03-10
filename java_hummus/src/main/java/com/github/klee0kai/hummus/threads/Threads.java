package com.github.klee0kai.hummus.threads;

import com.github.klee0kai.hummus.Hummus;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Threads {

    public static Thread.UncaughtExceptionHandler defUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    public static ThreadPoolExecutor newSingleThreadExecutor(String poolName) {
        return new ThreadPoolExecutor(0, 1, 0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new DefaultThreadFactory(poolName, false),
                Hummus.isDebug ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.DiscardPolicy()) {

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (CancellationException | ExecutionException ee) {
                        t = ee;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (t != null && defUncaughtExceptionHandler != null)
                    defUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
            }
        };
    }

    public static ThreadPoolExecutor newSingleThreadExecutor(String poolName, boolean daemon) {
        return new ThreadPoolExecutor(0, 1, 0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory(poolName, daemon),
                Hummus.isDebug ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.DiscardPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (CancellationException | ExecutionException ee) {
                        t = ee;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (t != null && defUncaughtExceptionHandler != null) {
                    defUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                } else {
                    Hummus.w(t);
                }
            }
        };
    }

    public static ThreadPoolExecutor newCachedPoolThreadExecutor(String poolName) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new DefaultThreadFactory(poolName, false),
                Hummus.isDebug ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.DiscardPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (CancellationException | ExecutionException ee) {
                        t = ee;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (t != null && defUncaughtExceptionHandler != null) {
                    defUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                } else {
                    Hummus.w(t);
                }
            }
        };
    }

    public static void startThread(Runnable r) {
        new Thread(r).start();
    }

    public static void trySleep(long millis) {
        if (millis <= 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static <T> T tryGet(Future<T> future) {
        try {
            return future.get();
        } catch (ExecutionException ignore) {
            //ignore
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

}
