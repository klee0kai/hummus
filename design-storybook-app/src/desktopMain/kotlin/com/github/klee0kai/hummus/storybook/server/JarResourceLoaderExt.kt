package com.github.klee0kai.hummus.storybook.server

import java.net.URL

private val contextClLoader by lazy {
    Thread.currentThread().getContextClassLoader()
}

fun findResourceFromWasmArtefacts(fileName: String): URL? {
    return contextClLoader.getResources(fileName).toList()
        .firstOrNull()
}
