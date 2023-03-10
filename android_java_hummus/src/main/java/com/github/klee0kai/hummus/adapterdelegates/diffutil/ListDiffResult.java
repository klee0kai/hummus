package com.github.klee0kai.hummus.adapterdelegates.diffutil;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListDiffResult<T> {

    private final DiffUtil.DiffResult diffResult;
    private final List<T> oldList, newList;

    public ListDiffResult(DiffUtil.DiffResult diffResult, List<T> oldList, List<T> newList) {
        this.diffResult = diffResult;
        this.oldList = oldList;
        this.newList = newList;
    }

    public void dispatchUpdatesTo(final RecyclerView.Adapter adapter) {
        if (diffResult != null) {
            diffResult.dispatchUpdatesTo(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    public void dispatchUpdatesTo(@NonNull IListUpdateCallbackExt updateCallback) {
        if (diffResult != null) {
            diffResult.dispatchUpdatesTo(updateCallback);
        } else {
            updateCallback.updateAll();
        }
    }

    public DiffUtil.DiffResult getDiffResult() {
        return diffResult;
    }

    public void applyTo(AbsDelegationAdapter<List<Object>> adapter) {
        int oldListLen = oldList != null ? oldList.size() : 0;
        int oldAdapterCount = adapter.getItemCount();

        adapter.setItems(newList != null ? new ArrayList<>(newList) : null);
        if (oldList == null || oldListLen != oldAdapterCount || diffResult == null) {
            adapter.notifyDataSetChanged();
        } else {
            diffResult.dispatchUpdatesTo(adapter);
        }
    }

}
