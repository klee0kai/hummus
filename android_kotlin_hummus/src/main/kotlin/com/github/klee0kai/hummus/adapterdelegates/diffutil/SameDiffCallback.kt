package com.github.klee0kai.hummus.adapterdelegates.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.github.klee0kai.hummus.model.ISameModel


open class SameDiffCallback(
    protected open val oldList: List<Any>?,
    protected open val newList: List<Any>?,
    protected open val changeAll: Boolean
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList?.size ?: 0

    override fun getNewListSize(): Int = newList?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldObject = oldList!![oldItemPosition]
        val newObject = newList!![newItemPosition]
        return if (oldObject is ISameModel) {
            oldObject.isSame(newObject)
        } else {
            oldObject == newObject
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldObject = oldList!![oldItemPosition]
        val newObject = newList!![newItemPosition]
        return !changeAll && oldObject == newObject
    }
}