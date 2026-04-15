package com.github.klee0kai.hummus.collections.sequence

fun <T> T.walkBreadthFirst(
    getChildren: (T) -> Iterable<T>,
): Sequence<T> = sequence {
    val queue = ArrayDeque<T>()
    queue.addLast(this@walkBreadthFirst)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        yield(current)

        queue.addAll(getChildren(current))
    }
}

fun <T> T.walkTopDown(
    getChildren: (T) -> Iterable<T>,
): Sequence<T> = sequence {
    yield(this@walkTopDown)

    for (child in getChildren(this@walkTopDown)) {
        yieldAll(child.walkTopDown(getChildren))
    }
}