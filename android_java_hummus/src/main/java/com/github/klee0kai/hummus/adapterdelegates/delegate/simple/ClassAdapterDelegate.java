package com.github.klee0kai.hummus.adapterdelegates.delegate.simple;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;

import java.util.List;

public abstract class ClassAdapterDelegate<T> extends AdapterDelegate<List<Object>> {

    @LayoutRes
    private final int layoutRes;
    private final Class<T> tClass;
    private final boolean forceTheSameClass;

    public ClassAdapterDelegate(Class<T> tClass, @LayoutRes int layoutRes, boolean forceTheSameClass) {
        this.tClass = tClass;
        this.layoutRes = layoutRes;
        this.forceTheSameClass = forceTheSameClass;
    }

    public ClassAdapterDelegate(Class<T> tClass, @LayoutRes int layoutRes) {
        this(tClass, layoutRes, false);
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
    protected SimpleViewHolder onCreateViewHolder(ViewGroup parent) {
        return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Object> objects, int i, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> list) {
        onBindViewHolder((T) objects.get(i), i, (SimpleViewHolder) viewHolder);
    }


    protected abstract void onBindViewHolder(T it, int pos, @NonNull SimpleViewHolder vh);

}
