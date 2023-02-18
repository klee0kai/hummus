package com.github.klee0kai.hummus.collections.gen;


import com.github.klee0kai.hummus.collections.interfaces.IJoin;
import com.github.klee0kai.hummus.model.Pair;

public class Joins {

    public interface IJoinSimple<T> {

        boolean isJoin(T it1, T it2);

    }

    public static <T> IJoin<T, T, T> simpleLeft(IJoinSimple<T> join) {
        return new IJoin<T, T, T>() {

            @Override
            public boolean isJoin(T it1, T it2) {
                return join.isJoin(it1, it2);
            }

            @Override
            public T join(T it1, T it2) {
                return it1;
            }
        };
    }


    public static <T> IJoin<T, T, T> simpleRight(IJoinSimple<T> join) {
        return new IJoin<T, T, T>() {

            @Override
            public boolean isJoin(T it1, T it2) {
                return join.isJoin(it1, it2);
            }

            @Override
            public T join(T it1, T it2) {
                return it2;
            }
        };
    }

    public static <T> IJoin<T, T, Pair<T, T>> simplePair(IJoinSimple<T> join) {
        return new IJoin<>() {

            @Override
            public boolean isJoin(T it1, T it2) {
                return join.isJoin(it1, it2);
            }

            @Override
            public Pair<T, T> join(T it1, T it2) {
                return new Pair<T, T>(it1, it2);
            }
        };
    }


}
