package com.github.klee0kai.hummus.adapterdelegates.delegate.simple

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate


abstract class ClassAdapterDelegate<T>(
    protected open val tClass: Class<T>,
    @field:LayoutRes @param:LayoutRes protected open val layoutRes: Int,
    protected open val forceTheSameClass: Boolean = false
) : AdapterDelegate<List<Any>>() {

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        val item = items[position]
        return if (forceTheSameClass) {
            tClass == item.javaClass
        } else {
            tClass.isInstance(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): SimpleViewHolder {
        return SimpleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutRes, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        objects: List<Any>,
        pos: Int,
        viewHolder: ViewHolder,
        list: List<Any>
    ) {
        onBindViewHolder(objects[pos] as T, pos, viewHolder as SimpleViewHolder)
    }

    protected abstract fun onBindViewHolder(it: T, pos: Int, vh: SimpleViewHolder)


    companion object {

        fun <T> create(
            tClass: Class<T>,
            @LayoutRes layoutRes: Int,
            forceTheSameClass: Boolean = false,
            bind: SimpleViewHolder.(it: T, pos: Int) -> Unit
        ): ClassAdapterDelegate<T> =
            object : ClassAdapterDelegate<T>(tClass, layoutRes, forceTheSameClass) {
                override fun onBindViewHolder(it: T, pos: Int, vh: SimpleViewHolder) {
                    bind.invoke(vh, it, pos)
                }
            }

    }
}
