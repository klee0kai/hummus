package com.github.klee0kai.hummus.adapterdelegates.delegate.viewbinding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;

import java.util.List;

public abstract class ClassViewBindingAdapterDelegate<T, VB extends ViewBinding> extends AdapterDelegate<List<Object>> {

    private final Class<T> tClass;
    private final boolean forceTheSameClass;

    public ClassViewBindingAdapterDelegate(Class<T> tClass, boolean forceTheSameClass) {
        this.tClass = tClass;
        this.forceTheSameClass = forceTheSameClass;
    }

    public ClassViewBindingAdapterDelegate(Class<T> tClass) {
        this(tClass, false);
    }

    @Override
    protected boolean isForViewType(@NonNull List<Object> items, int position) {
        Object item = items.get(position);
        if (forceTheSameClass) {
            return tClass.equals(item.getClass());
        } else {
            return tClass.isInstance(item);
        }
    }

    @NonNull
    @Override
    protected ViewBindingHolder<VB> onCreateViewHolder(ViewGroup parent) {
        return new ViewBindingHolder<>(onCreateViewBinding(LayoutInflater.from(parent.getContext()), parent));
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Object> objects, int i, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> list) {
        onBindViewHolder((T) objects.get(i), i, (ViewBindingHolder<VB>) viewHolder);
    }


    protected abstract VB onCreateViewBinding(LayoutInflater inflater, ViewGroup parent);

    protected abstract void onBindViewHolder(T it, int pos, @NonNull ViewBindingHolder<VB> vh);

}
