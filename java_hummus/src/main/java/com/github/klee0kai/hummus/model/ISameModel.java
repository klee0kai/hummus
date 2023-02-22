package com.github.klee0kai.hummus.model;

public interface ISameModel {

    /**
     * The models are same (ids is equals) but can have different other data
     *
     * @param ob
     * @return
     */
    boolean isSame(Object ob);

}
