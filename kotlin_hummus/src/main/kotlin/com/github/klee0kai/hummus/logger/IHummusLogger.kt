package com.github.klee0kai.hummus.logger

interface IHummusLogger {

    fun w(text: String?, vararg args: Any?)

    fun w(t: Throwable?)
}