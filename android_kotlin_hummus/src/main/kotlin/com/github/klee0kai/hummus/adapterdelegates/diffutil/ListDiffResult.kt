package com.github.klee0kai.hummus.adapterdelegates.diffutil

import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter

data class ListDiffResult<T>(
    val diffResult: DiffResult?,
    val oldList: List<T>?,
    val newList: List<T>?
)

fun <T> ListDiffResult<T>.dispatchUpdatesTo(adapter: RecyclerView.Adapter<*>) {
    if (diffResult != null) {
        diffResult.dispatchUpdatesTo(adapter)
    } else {
        adapter.notifyDataSetChanged()
    }
}

fun <T> ListDiffResult<T>.dispatchUpdatesTo(updateCallback: IListUpdateCallbackExt) {
    if (diffResult != null) {
        diffResult.dispatchUpdatesTo(updateCallback)
    } else {
        updateCallback.updateAll()
    }
}

fun <T : Any> ListDiffResult<T>.applyTo(adapter: AbsDelegationAdapter<List<Any>>) {
    val oldListLen = oldList?.size ?: 0
    val oldAdapterCount = adapter.itemCount
    adapter.items = newList

    if (oldList == null || oldListLen != oldAdapterCount || diffResult == null) {
        adapter.notifyDataSetChanged()
    } else {
        diffResult.dispatchUpdatesTo(adapter)
    }
}
