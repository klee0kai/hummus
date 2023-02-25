package com.github.klee0kai.hummus.adapterdelegates.diffutil

import androidx.recyclerview.widget.DiffUtil

open class SameDiffUtilHelper<T : Any> {

    protected open var oldList: MutableList<T>? = null
    protected open var diffResult: ListDiffResult<T>? = null

    fun saveOld(old: List<T>?) {
        if (old == null) {
            oldList = null
            return
        }
        val len = old.size
        if (len > MAX_LIST_SIZE) {
            //big list size for diff util
            return
        }


        // we not prefer use deep copy for kotlin
        // use immutable data classes to protect data and for correct diff util working
        oldList = mutableListOf()
        oldList?.addAll(old)
    }

    fun calculateWith(newList: List<T>?, detectMoves: Boolean = false): ListDiffResult<T> {
        if (oldList == null) {
            // nothing to compare
            return ListDiffResult(null, null, newList).also { diffResult = it }
        }
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            SameDiffCallback(
                oldList, newList, false
            ),
            detectMoves
        )
        diffResult = ListDiffResult(result, oldList, newList)
        oldList = null
        return diffResult!!
    }

    fun popDiffResult(list: List<T>?): ListDiffResult<T> {
        val changes = diffResult
        diffResult = null
        return changes ?: ListDiffResult(null, null, list)
    }

    companion object {
        private const val MAX_LIST_SIZE = 10000
    }
}