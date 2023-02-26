package com.github.klee0kai.hummus.adapterdelegates.delegate.viewbinding;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class ViewBindingHolder<T extends ViewBinding> extends RecyclerView.ViewHolder {

    public final T binding;

    public ViewBindingHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

}
