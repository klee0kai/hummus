package com.github.klee0kai.hummus.adapterdelegates.delegate.viewbinding

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

open class ViewBindingHolder<T : ViewBinding>(
    open val binding: T
) : ViewHolder(binding.root)

