package com.github.klee0kai.hummus.adapterdelegates.diffutil;

import androidx.recyclerview.widget.DiffUtil;

import com.github.klee0kai.hummus.Hummus;
import com.github.klee0kai.hummus.model.ICloneable;

import java.util.ArrayList;
import java.util.List;

public class SameDiffUtilHelper<T extends ICloneable> {

    private static final int MAX_LIST_SIZE = 10_000;
    private List<T> oldList = null;

    private SameDiffResult<T> diffResult = null;

    public SameDiffUtilHelper() {
    }

    public void saveOld(List<T> old) {
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
        for (ICloneable item : old) {
            try {
                oldList.add((T) item.clone());
            } catch (CloneNotSupportedException e) {
                Hummus.w(e);
            }
        }
    }

    public SameDiffResult<T> calculateWith(List<T> newList, boolean detectMoves) {
        if (oldList == null) {
            // nothing to compare
            return diffResult = new SameDiffResult<>(null, null, newList);
        }

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                new SameDiffCallback(
                        new ArrayList<>(oldList),
                        newList != null ? new ArrayList<>(newList) : null,
                        false
                ),
                detectMoves);
        diffResult = new SameDiffResult<>(result, oldList, newList);
        oldList = null;
        return diffResult;
    }

    public SameDiffResult<T> calculateWith(List<T> newList) {
        return calculateWith(newList, false);
    }


    public SameDiffResult<T> popDiffResult(List<T> list) {
        SameDiffResult<T> changes = diffResult;
        diffResult = null;
        return changes != null ? changes : new SameDiffResult<>(null, null, list);
    }


}
