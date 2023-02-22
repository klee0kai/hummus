package com.github.klee0kai.hummus.model;

public class CloneableHelper {

    public static <T extends ICloneable> T tryClone(T origin, T elseVal) {
        try {
            return origin != null ? (T) origin.clone() : elseVal;
        } catch (CloneNotSupportedException e) {
            return elseVal;
        }
    }

}
