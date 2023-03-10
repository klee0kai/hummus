package com.github.klee0kai.hummus.adapterdelegates.diffutil;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * dispatch updates with offset
 */
public class OffsetUpdateCallback implements IListUpdateCallbackExt {

    private final int offset;
    private RecyclerView.Adapter adapter;

    public OffsetUpdateCallback(RecyclerView.Adapter adapter, int offset) {
        this.offset = offset;
        this.adapter = adapter;
    }


    public OffsetUpdateCallback(RecyclerView.Adapter adapter) {
        this(adapter, 0);
    }


    @Override
    public void onInserted(int position, int count) {
        adapter.notifyItemRangeInserted(position + offset, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        adapter.notifyItemRangeRemoved(position + offset, count);

    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        adapter.notifyItemMoved(fromPosition + offset, toPosition + offset);
    }

    @Override
    public void onChanged(int position, int count, @Nullable Object payload) {
        adapter.notifyItemRangeChanged(position + offset, count, payload);
    }

    @Override
    public void updateAll() {
        adapter.notifyDataSetChanged();
    }
}
