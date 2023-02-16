package com.github.klee0kai.android_devkit.threads;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadTests {

    private static final int RUN_COUNT = 300;

    @Test(timeout = 100)
    public void sequence_run() throws ExecutionException, InterruptedException {
        //Give
        LinkedList<Integer> runs = new LinkedList<>();
        ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("secThread");

        //When
        Future lastFuture = null;
        for (int i = 0; i < RUN_COUNT; i++) {
            int finalI = i;
            lastFuture = secThread.submit(() -> {
                runs.add(finalI);
            });
        }
        lastFuture.get();

        //then
        for (int i = 0; i < RUN_COUNT; i++) {
            assertEquals(i, runs.get(i).intValue());
        }

    }
}
