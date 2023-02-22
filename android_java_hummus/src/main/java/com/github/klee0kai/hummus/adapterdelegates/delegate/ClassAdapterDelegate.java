package com.github.klee0kai.hummus.adapterdelegates.delegate;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.github.klee0kai.hummus.adapterdelegates.SimpleViewHolder;
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;

import java.util.List;

public abstract class ClassAdapterDelegate<T> extends AdapterDelegate<List<Object>> {

    @LayoutRes
    private final int layoutRes;
    private final Class<T> tClass;

    public ClassAdapterDelegate(Class<T> tClass, @LayoutRes int layoutRes) {
        this.tClass = tClass;
        this.layoutRes = layoutRes;
    }

    @Override
    protected boolean isForViewType(@NonNull List<Object> items, int position) {
        return tClass.isInstance(items.get(position));
    }

    @NonNull
    @Override
    protected SimpleViewHolder onCreateViewHolder(ViewGroup parent) {
        return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Object> objects, int i, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> list) {
        onBindViewHolder((T) objects.get(i), i, (SimpleViewHolder) viewHolder, objects, list);
    }


    protected abstract void onBindViewHolder(T it, int pos, @NonNull SimpleViewHolder vh, @NonNull List<Object> allObjects, @NonNull List<Object> allOldlist);

}
