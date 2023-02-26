package com.github.klee0kai.hummus.logger;

public interface IHummusLogger {

    void w(String text, Object... args);

    void w(Throwable t);

}
