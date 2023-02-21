package com.github.klee0kai.hummus.arch.mvp;

import com.github.klee0kai.hummus.collections.weaklist.WeakList;
import com.github.klee0kai.hummus.threads.AndroidThreads;

public class WeakViewsList extends WeakList<IRefreshView> {

    public void refreshAllViews() {
        AndroidThreads.runMain(() -> {
            for (IRefreshView v : this)
                v.refreshUI();
        });
    }

    public void refreshAllViews(int delay) {
        AndroidThreads.runMainDelayed(delay, () -> {
            for (IRefreshView v : this)
                v.refreshUI();
        });
    }

}
