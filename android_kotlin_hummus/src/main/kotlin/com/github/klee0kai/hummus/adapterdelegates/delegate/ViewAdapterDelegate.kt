package com.github.klee0kai.hummus.adapterdelegates.delegate

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.klee0kai.hummus.adapterdelegates.SimpleViewHolder
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

open class ViewAdapterDelegate(
    protected open val layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
) : AdapterDelegate<List<Any>>() {

    constructor(@RecyclerView.Orientation orientation: Int) :
            this(
                when (orientation) {
                    RecyclerView.HORIZONTAL -> ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    else -> ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            )


    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is View
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val frameLayout = FrameLayout(parent.context)
        frameLayout.layoutParams = ViewGroup.LayoutParams(layoutParams)
        return SimpleViewHolder(frameLayout)
    }

    override fun onBindViewHolder(objects: List<Any>, i: Int, vh: ViewHolder, list: List<Any>) {
        val container = vh.itemView as ViewGroup

        // detach from current parent
        val view = objects[i] as View
        (view.parent as? ViewGroup)?.removeView(view)

        // attach to new parent
        container.apply {
            removeAllViews()
            addView(view)
        }
    }
}