package com.github.klee0kai.crossbox.processor.exceptions

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

fun <T : KSNode> Sequence<T>.forEachKsNode(
    action: (index: Int, T) -> Unit
) {
    forEachIndexed { idx, func ->
        wrapKsNoteInfo(func) {
            action(idx, func)
        }
    }
}


fun <T : KSNode> Iterable<T>.forEachKsNode(
    action: (index: Int, T) -> Unit
) {
    forEachIndexed { idx, func ->
        wrapKsNoteInfo(func) {
            action(idx, func)
        }
    }
}

fun <T : KSNode, R> Sequence<T>.mapKsNode(
    action: (index: Int, T) -> R
) = mapIndexed { idx, func ->
    wrapKsNoteInfo(func) {
        action(idx, func)
    }
}


fun <T : KSNode, R> Iterable<T>.mapKsNode(
    action: (index: Int, T) -> R
) = mapIndexed { idx, func ->
    wrapKsNoteInfo(func) {
        action(idx, func)
    }
}


