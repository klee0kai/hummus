@file:OptIn(ExperimentalUuidApi::class)

package com.github.klee0kai.hummus.common

import kotlinx.atomicfu.atomic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object Dummy {

    private val dummyIdCounter = atomic(41)

    val dummyId get() = dummyIdCounter.incrementAndGet()

    val unicString get() = Uuid.random().toString()

}