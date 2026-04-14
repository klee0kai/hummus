package com.github.klee0kai.crossbox.processor.exceptions

import com.google.devtools.ksp.symbol.KSNode


open class KspBaseException(
    message: String? = null,
    cause: Throwable? = null,
    val element: KSNode? = null,
) : IllegalStateException(message, cause) {

    fun findErrorElement(): KSNode? {
        var sourceElement: KSNode? = null
        if (cause is KspBaseException) {
            sourceElement = (cause as KspBaseException).findErrorElement()
        }
        if (sourceElement == null) sourceElement = element
        return sourceElement
    }

}
