package com.github.klee0kai.hummus.adapterdelegates.delegate

import android.view.View
import com.github.klee0kai.hummus.adapterdelegates.delegate.viewbinding.ClassViewBindingAdapterDelegate
import com.github.klee0kai.hummus.databinding.ItemLabelHorizontalBinding

object AdapterDelegates {

    inline fun <reified T> labelDelegate(
        crossinline block: (T) -> String = { it.toString() },
        noinline onClick: ((View) -> Unit)? = null,
        noinline onLongClick: ((View) -> Boolean)? = null,
    ): ClassViewBindingAdapterDelegate<T, ItemLabelHorizontalBinding> {
        return ClassViewBindingAdapterDelegate.create(
            T::class.java,
            bindingCreator = { inf, container ->
                ItemLabelHorizontalBinding.inflate(inf, container, false)
            }
        ) { it, _ ->
            binding.apply {
                label.text = block(it)

                if (onClick != null) root.setOnClickListener(onClick)
                if (onLongClick != null) root.setOnLongClickListener(onLongClick)
            }
        }
    }

}