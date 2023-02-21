package com.github.klee0kai.hummus.arch.mvp

import java.util.*

open class SimplePresenter : ISimplePresenter {

    protected val views = WeakViewsList()
    protected val viewsStates = WeakHashMap<String?, Any?>()

    override fun getState(stateId: String?): Any? {
        return viewsStates[stateId]
    }

    override fun saveState(stateId: String?, state: Any?) {
        viewsStates[stateId] = state
    }

    override fun subscribe(view: IRefreshView?) {
        views.add(view)
    }

    override fun unsubscribe(view: IRefreshView?) {
        views.remove(view)
    }
}