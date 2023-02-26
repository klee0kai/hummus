package com.github.klee0kai.hummus.collections.compare;

import com.github.klee0kai.hummus.interfaces.ICallBackResult;

import java.util.Comparator;
import java.util.Locale;

public class Comparators {

    public enum SortType {
        ASC,
        DESC
    }

    public static <T> Comparator<T> byNotNull(SortType sortType) {
        return byBoolean(sortType, it -> it != null);
    }

    public static <T> Comparator<T> byNotNull(SortType sortType, ICallBackResult<T, Object> provide) {
        return byBoolean(sortType, it -> provide.call(it) != null);
    }

    public static <T> Comparator<T> byHashCode(SortType sortType) {
        return byInt(sortType, it -> it.hashCode());
    }

    public static <T> Comparator<T> byType(SortType sortType) {
        return byString(sortType, it -> it.getClass().getCanonicalName());
    }

    public static <T> Comparator<T> byString(SortType sortType, ICallBackResult<T, String> provide) {
        return (it1, it2) -> stringCompare(provide.call(it1), provide.call(it2), sortType);
    }

    public static Comparator<String> byString(SortType sortType) {
        return (it1, it2) -> stringCompare(it1, it2, sortType);
    }

    public static <T> Comparator<T> byInt(SortType sortType, ICallBackResult<T, Integer> provide) {
        return (it1, it2) -> intCompare(provide.call(it1), provide.call(it2), sortType);
    }

    public static Comparator<Integer> byInt(SortType sortType) {
        return (it1, it2) -> intCompare(it1, it2, sortType);
    }

    public static <T> Comparator<T> byLong(SortType sortType, ICallBackResult<T, Long> provide) {
        return (it1, it2) -> longCompare(provide.call(it1), provide.call(it2), sortType);
    }


    public static <T> Comparator<T> byFloat(SortType sortType, ICallBackResult<T, Float> provide) {
        return (it1, it2) -> floatCompare(provide.call(it1), provide.call(it2), sortType);
    }

    public static <T> Comparator<T> byDouble(SortType sortType, ICallBackResult<T, Double> provide) {
        return (it1, it2) -> doubleCompare(provide.call(it1), provide.call(it2), sortType);
    }


    public static <T> Comparator<T> byBoolean(SortType sortType, ICallBackResult<T, Boolean> provide) {
        return (it1, it2) -> booleanCompare(provide.call(it1), provide.call(it2), sortType);
    }


    public static int notNullsCompare(Object it1, Object it2, SortType sortType) {
        boolean null1 = it1 != null;
        boolean null2 = it2 != null;
        return sortType == SortType.ASC ? Boolean.compare(null1, null2) : Boolean.compare(null2, null1);
    }

    public static int stringCompare(String it1, String it2, SortType sortType) {
        int r = notNullsCompare(it1, it2, sortType);
        if (r != 0) {
            return r;
        }
        if (it1 == null || it2 == null) {
            return 0;
        }
        return sortType == SortType.ASC ? it1.compareTo(it2) : it2.compareTo(it1);
    }

    public static int stringCompareIgnoreCase(String it1, String it2, SortType sortType) {
        int r = notNullsCompare(it1, it2, sortType);
        if (r != 0) {
            return r;
        }
        if (it1 == null || it2 == null) {
            return 0;
        }
        return sortType == SortType.ASC ? it1.toLowerCase(Locale.ROOT).compareTo(it2.toLowerCase(Locale.ROOT)) :
                it2.toLowerCase(Locale.ROOT).compareTo(it1.toLowerCase(Locale.ROOT));
    }

    public static int intCompare(Integer it1, Integer it2, SortType sortType) {
        int r = notNullsCompare(it1, it2, sortType);
        if (r != 0) {
            return r;
        }
        if (it1 == null || it2 == null) {
            return 0;
        }
        return sortType == SortType.ASC ? Integer.compare(it1, it2) : Integer.compare(it2, it1);
    }

    public static int longCompare(Long it1, Long it2, SortType sortType) {
        int r = notNullsCompare(it1, it2, sortType);
        if (r != 0) {
            return r;
        }
        if (it1 == null || it2 == null) {
            return 0;
        }
        return sortType == SortType.ASC ? Long.compare(it1, it2) : Long.compare(it2, it1);
    }

    public static int floatCompare(Float it1, Float it2, SortType sortType) {
        int r = notNullsCompare(it1, it2, sortType);
        if (r != 0) {
            return r;
        }
        if (it1 == null || it2 == null) {
            return 0;
        }
        return sortType == SortType.ASC ? Float.compare(it1, it2) : Float.compare(it2, it1);
    }

    public static int doubleCompare(Double it1, Double it2, SortType sortType) {
        int r = notNullsCompare(it1, it2, sortType);
        if (r != 0) {
            return r;
        }
        if (it1 == null || it2 == null) {
            return 0;
        }
        return sortType == SortType.ASC ? Double.compare(it1, it2) : Double.compare(it2, it1);
    }

    public static int booleanCompare(Boolean it1, Boolean it2, SortType sortType) {
        int r = notNullsCompare(it1, it2, sortType);
        if (r != 0) {
            return r;
        }
        if (it1 == null || it2 == null) {
            return 0;
        }
        return sortType == SortType.ASC ? Boolean.compare(it1, it2) : Boolean.compare(it2, it1);
    }

    public static <T> Comparator<T> firstNotZero(Comparator<T>... compares) {
        return (it1, it2) -> {
            if (compares != null) for (Comparator<T> c : compares) {
                int r = c.compare(it1, it2);
                if (r != 0)
                    return r;
            }
            return 0;
        };
    }

}
