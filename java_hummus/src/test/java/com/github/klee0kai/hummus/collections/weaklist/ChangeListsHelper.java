package com.github.klee0kai.hummus.collections.weaklist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.klee0kai.hummus.interfaces.ICallBackResult;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ChangeListsHelper {

    public static <T, R> void changeForEachListsSame(List<List<T>> lists, ICallBackResult<List<T>, R> callable) {
        R qualityResult = null;
        for (List<T> l : lists) {
            R res = callable.call(l);

            if (qualityResult != null) assertEquals(qualityResult, res);
            else qualityResult = res;
        }
    }


    public static <T> void assertListsSame(List<T> expected, List<T> actual, T[] emptyArray) {
        assertEquals(expected.isEmpty(), actual.isEmpty());
        assertEquals(expected.size(), actual.size());

        assertEquals(
                Arrays.asList(expected.toArray()),
                Arrays.asList(actual.toArray())
        );
        assertEquals(
                Arrays.asList(expected.toArray(emptyArray)),
                Arrays.asList(actual.toArray(emptyArray))
        );


        {
            // iterators are same
            Iterator<T> expIt = expected.iterator();
            Iterator<T> actIt = expected.iterator();
            while (expIt.hasNext()) {
                assertTrue(actIt.hasNext());

                T extItem = expIt.next();
                T actItem = actIt.next();
                assertEquals(extItem, actItem);
                assertEquals(expected.indexOf(extItem), actual.indexOf(actItem));
                assertEquals(expected.lastIndexOf(extItem), actual.lastIndexOf(actItem));
                assertEquals(expected.contains(extItem), actual.contains(actItem));
            }
            assertFalse(actIt.hasNext());
        }

        {
            // listiterators are same
            Iterator<T> expIt = expected.listIterator();
            Iterator<T> actIt = expected.listIterator();
            while (expIt.hasNext()) {
                assertTrue(actIt.hasNext());

                T extItem = expIt.next();
                T actItem = actIt.next();
                assertEquals(extItem, actItem);
                assertEquals(expected.indexOf(extItem), actual.indexOf(actItem));
                assertEquals(expected.contains(extItem), actual.contains(actItem));
            }
            assertFalse(actIt.hasNext());
        }

        //contains each other
        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));
    }

    public static class SimpleIntPair {
        int a, b;

        public SimpleIntPair(int a) {
            this.a = a;
        }

        public SimpleIntPair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimpleIntPair that = (SimpleIntPair) o;
            return a == that.a && b == that.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

}
