package com.github.klee0kai.hummus.adapterdelegates.delegate.simple;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;

import java.util.List;

public class ViewAdapterDelegate extends AdapterDelegate<List<Object>> {

    private final ViewGroup.LayoutParams layoutParams;

    public ViewAdapterDelegate(ViewGroup.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public ViewAdapterDelegate() {
        this(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    public ViewAdapterDelegate(@RecyclerView.Orientation int orientation) {
        switch (orientation) {
            case RecyclerView.HORIZONTAL:
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                break;

            case RecyclerView.VERTICAL:
            default:
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
        }

    }

    @Override
    protected boolean isForViewType(@NonNull List<Object> items, int position) {
        return items.get(position) instanceof View;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(layoutParams));
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
