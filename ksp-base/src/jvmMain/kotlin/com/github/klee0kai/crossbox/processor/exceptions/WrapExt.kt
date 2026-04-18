package com.github.klee0kai.crossbox.processor.exceptions

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode

inline fun <T, R> T.wrapKsNoteInfo(
    ksNode: KSNode?,
    block: T.() -> R,
): R {
    return try {
        block()
    } catch (e: Throwable) {
        throw KspBaseException(
            message = "${e.message}. At ${ksNode?.location}  ",
            element = ksNode,
            cause = e,
        )
    }
}

fun <T : KSAnnotated> Sequence<T>.forEachAnnotated(
    action: (index: Int, T) -> Unit
) {
    forEachIndexed { idx, func ->
        wrapKsNoteInfo(func) {
            action(idx, func)
        }
    }
}


fun <T : KSAnnotated> Iterable<T>.forEachAnnotated(
    action: (index: Int, T) -> Unit
) {
    forEachIndexed { idx, func ->
        wrapKsNoteInfo(func) {
            action(idx, func)
        }
    }
}
