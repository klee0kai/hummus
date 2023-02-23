package com.github.klee0kai.hummus.adapterdelegates.diffutil;


import androidx.recyclerview.widget.DiffUtil;

import com.github.klee0kai.hummus.model.ISameModel;

import java.util.List;
import java.util.Objects;

public class SameDiffCallback extends DiffUtil.Callback {

    private final List<Object> oldList;
    private final List<Object> newList;
    private final boolean changeAll;

    public SameDiffCallback(List<Object> oldList, List<Object> newList, boolean changeAll) {
        this.oldList = oldList;
        this.newList = newList;
        this.changeAll = changeAll;
    }


    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldObject = oldList.get(oldItemPosition);
        Object newObject = newList.get(newItemPosition);
        if (oldObject instanceof ISameModel) {
            return ((ISameModel) oldObject).isSame(newObject);
        } else {
            return Objects.equals(oldObject, newObject);
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldObject = oldList.get(oldItemPosition);
        Object newObject = newList.get(newItemPosition);
        return !changeAll && Objects.equals(oldObject, newObject);
    }
}
