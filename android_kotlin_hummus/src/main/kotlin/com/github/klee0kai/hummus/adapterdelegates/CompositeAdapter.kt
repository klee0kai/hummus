package com.github.klee0kai.hummus.adapterdelegates

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

open class CompositeAdapter(
    delegatesManager: AdapterDelegatesManager<List<Any>>
) : ListDelegationAdapter<List<Any>>(delegatesManager) {

    companion object {

        fun create(vararg adapterDelegates: AdapterDelegate<List<Any>>): CompositeAdapter =
            CompositeAdapter(
                AdapterDelegatesManager<List<Any>>().apply {
                    adapterDelegates.onEach {
                        addDelegate(it)
                    }
                }
            )

    }
}