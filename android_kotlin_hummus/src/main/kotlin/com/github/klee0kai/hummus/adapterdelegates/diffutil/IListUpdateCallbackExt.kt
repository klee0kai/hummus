package com.github.klee0kai.hummus.adapterdelegates.diffutil

import androidx.recyclerview.widget.ListUpdateCallback

interface IListUpdateCallbackExt : ListUpdateCallback {
    fun updateAll()
}