package com.github.klee0kai.hummus.adapterdelegates.delegate;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.github.klee0kai.hummus.adapterdelegates.SimpleViewHolder;
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;

import java.util.List;

public class ViewAdapterDelegate extends AdapterDelegate<List<Object>> {


    @Override
    protected boolean isForViewType(@NonNull List<Object> items, int position) {
        return items.get(position) instanceof View;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new SimpleViewHolder(frameLayout);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Object> objects, int i, @NonNull RecyclerView.ViewHolder vh, @NonNull List<Object> list) {
        ViewGroup container = (ViewGroup) vh.itemView;
        View v = (View) objects.get(i);
        if (v.getParent() != null)
            ((ViewGroup) v.getParent()).removeView(v);

        container.removeAllViews();
        container.addView(v);
    }

}