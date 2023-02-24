package com.github.klee0kai.hummus.adapterdelegates.diffutil

import androidx.recyclerview.widget.RecyclerView

/**
 * dispatch updates with offset
 */
open class OffsetUpdateCallback(
    protected open val adapter: RecyclerView.Adapter<*>,
    protected open val offset: Int = 0
) : IListUpdateCallbackExt {

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position + offset, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position + offset, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition + offset, toPosition + offset)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(position + offset, count, payload)
    }

    override fun updateAll() {
        adapter.notifyDataSetChanged()
    }
}