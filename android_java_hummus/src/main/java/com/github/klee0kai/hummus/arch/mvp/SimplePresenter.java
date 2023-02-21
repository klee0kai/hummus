package com.github.klee0kai.hummus.arch.mvp;

import java.util.WeakHashMap;

public class SimplePresenter implements ISimplePresenter {

    protected final WeakViewsList views = new WeakViewsList();
    protected final WeakHashMap<String, Object> viewsStates = new WeakHashMap<>();

    @Override
    public Object getState(String stateId) {
        return viewsStates.get(stateId);
    }

    @Override
    public void saveState(String stateId, Object state) {
        viewsStates.put(stateId, state);
    }


    @Override
    public void subscribe(IRefreshView view) {
        views.add(view);
    }

    @Override
    public void unsubscribe(IRefreshView view) {
        views.remove(view);
    }

}
