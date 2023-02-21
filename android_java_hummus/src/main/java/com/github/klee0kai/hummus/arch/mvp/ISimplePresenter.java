package com.github.klee0kai.hummus.arch.mvp;

public interface ISimplePresenter {

    /**
     * get view state by key
     */
    Object getState(String stateId);

    /**
     * safe view state by key
     */
    void saveState(String stateId, Object state);

    /**
     * subscribe view for data update
     */
    void subscribe(IRefreshView view);

    void unsubscribe(IRefreshView view);

}
