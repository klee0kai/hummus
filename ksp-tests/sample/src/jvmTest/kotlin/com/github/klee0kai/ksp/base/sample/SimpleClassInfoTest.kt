package com.github.klee0kai.ksp.base.sample

import com.github.klee0kai.ksp.base.sample.processor.model.GenClassInfo
import kotlin.test.Test
import kotlin.test.assertEquals

@GenClassInfo
class SampleClass

class SimpleClassInfoTest {

    @Test
    fun checkClassInfo() {
        val cl = SampleClass()

        val clInfo = cl.classInfo

        assertEquals("com.github.klee0kai.ksp.base.sample", clInfo.packageName)
        assertEquals("SampleClass", clInfo.simpleName)
    }

}