package com.github.klee0kai.hummus.adapterdelegates.delegate.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate


abstract class ClassViewBindingAdapterDelegate<T, VB : ViewBinding>(
    protected open val tClass: Class<T>,
    protected open val forceTheSameClass: Boolean = false,
    protected open val bindingCreator: (LayoutInflater, ViewGroup) -> VB
) : AdapterDelegate<List<Any>>() {

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        val item = items[position]
        return if (forceTheSameClass) {
            tClass == item.javaClass
        } else {
            tClass.isInstance(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewBindingHolder<VB> {
        return ViewBindingHolder(bindingCreator(LayoutInflater.from(parent.context), parent))
    }

    override fun onBindViewHolder(
        objects: List<Any>,
        pos: Int,
        viewHolder: ViewHolder,
        list: List<Any>
    ) {
        onBindViewHolder(objects[pos] as T, pos, viewHolder as ViewBindingHolder<VB>)
    }

    protected abstract fun onBindViewHolder(it: T, pos: Int, vh: ViewBindingHolder<VB>)


    companion object {

        fun <T, VB : ViewBinding> create(
            tClass: Class<T>,
            forceTheSameClass: Boolean = false,
            bindingCreator: (LayoutInflater, ViewGroup) -> VB,
            bind: ViewBindingHolder<VB>.(it: T, pos: Int) -> Unit,
        ): ClassViewBindingAdapterDelegate<T, VB> {
            return object :
                ClassViewBindingAdapterDelegate<T, VB>(tClass, forceTheSameClass, bindingCreator) {

                override fun onBindViewHolder(it: T, pos: Int, vh: ViewBindingHolder<VB>) {
                    bind.invoke(vh, it, pos)
                }

            }
        }

    }
}
