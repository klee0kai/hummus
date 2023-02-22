package com.github.klee0kai.hummus.arch.mvp

interface ISimplePresenter {
    /**
     * get view state by key
     */
    fun getState(stateId: String?): Any?

    /**
     * safe view state by key
     */
    fun saveState(stateId: String?, state: Any?)

    /**
     * subscribe view for data update
     */
    fun subscribe(view: IRefreshView?)
    fun unsubscribe(view: IRefreshView?)
}