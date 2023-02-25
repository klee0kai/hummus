package com.github.klee0kai.hummus.adapterdelegates.diffutil;

import androidx.recyclerview.widget.DiffUtil;

import com.github.klee0kai.hummus.model.CloneableHelper;
import com.github.klee0kai.hummus.model.ICloneable;

import java.util.ArrayList;
import java.util.List;

public class SameDiffUtilHelper<T extends ICloneable> {

    public static final int MAX_LIST_SIZE = 10_000;

    private List<T> oldList = null;
    private ListDiffResult<T> diffResult = null;

    public void saveOld(List<T> old, boolean deepCopy) {
        if (old == null) {
            this.oldList = null;
            return;
        }
        int len = old.size();
        if (len > MAX_LIST_SIZE) {
            //big list size for diff util
            return;
        }
        this.oldList = new ArrayList<>(len);
        if (deepCopy) {
            for (T item : old) {
                oldList.add(CloneableHelper.tryClone(item, item));
            }
        } else {
            this.oldList.addAll(old);
        }
    }

    public ListDiffResult<T> calculateWith(List<T> newList, boolean detectMoves) {
        if (oldList == null) {
            // nothing to compare
            return diffResult = new ListDiffResult<>(null, null, newList);
        }

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new SameDiffCallback(
                        new ArrayList<>(oldList),
                        newList != null ? new ArrayList<>(newList) : null,
                        false
                ),
                detectMoves);
        diffResult = new ListDiffResult<>(result, oldList, newList);
        oldList = null;
        return diffResult;
    }

    public ListDiffResult<T> calculateWith(List<T> newList) {
        return calculateWith(newList, false);
    }


    public ListDiffResult<T> popDiffResult(List<T> list) {
        ListDiffResult<T> changes = diffResult;
        diffResult = null;
        return changes != null ? changes : new ListDiffResult<>(null, null, list);
    }


}
