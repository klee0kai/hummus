package com.github.klee0kai.android_devkit.collections.weaklist

import org.junit.Assert.*

object ChangeListsHelper {

    fun <T, R> changeForEachListsSame(
        vararg lists: MutableList<T>,
        block: MutableList<T>.() -> R
    ) {
        var qualityResult: R? = null
        for (l in lists) {
            val res: R = block(l)
            if (qualityResult != null) assertEquals(qualityResult, res) else qualityResult =
                res
        }
    }

    inline fun <reified T> assertListsSame(
        expected: List<T>,
        actual: List<T>,
    ) {
        assertEquals(expected.isEmpty(), actual.isEmpty())
        assertEquals(expected.size.toLong(), actual.size.toLong())
        assertEquals(
            expected.toTypedArray().toList(),
            actual.toTypedArray().toList(),
        )
        run {

            // iterators are same
            val expIt = expected.iterator()
            val actIt = expected.iterator()
            while (expIt.hasNext()) {
                assertTrue(actIt.hasNext())
                val extItem = expIt.next()
                val actItem = actIt.next()
                assertEquals(extItem, actItem)
                assertEquals(
                    expected.indexOf(extItem).toLong(),
                    actual.indexOf(actItem).toLong()
                )
                assertEquals(
                    expected.lastIndexOf(extItem).toLong(),
                    actual.lastIndexOf(actItem).toLong()
                )
                assertEquals(expected.contains(extItem), actual.contains(actItem))
            }
            assertFalse(actIt.hasNext())
        }

        run {
            // listiterators are same
            val expIt: Iterator<T> = expected.listIterator()
            val actIt: Iterator<T> = expected.listIterator()
            while (expIt.hasNext()) {
                assertTrue(actIt.hasNext())
                val extItem = expIt.next()
                val actItem = actIt.next()
                assertEquals(extItem, actItem)
                assertEquals(
                    expected.indexOf(extItem).toLong(),
                    actual.indexOf(actItem).toLong()
                )
                assertEquals(expected.contains(extItem), actual.contains(actItem))
            }
            assertFalse(actIt.hasNext())
        }

        //contains each other
        assertTrue(actual.containsAll(expected))
        assertTrue(expected.containsAll(actual))
    }

}