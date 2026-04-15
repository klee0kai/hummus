package com.github.klee0kai.hummus.coroutine

import com.github.klee0kai.hummus.common.Dummy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object GlobalJobsCollection {

    val globalJobs = MutableStateFlow(emptyList<GlobalJob>())

    inline fun <R> trackJob(
        descriptionRes: Int = 0,
        block: () -> R,
    ): R {
        val runId = Dummy.dummyId
        try {
            if (descriptionRes != 0) {
                globalJobs.update { jobs ->
                    jobs + listOf(
                        GlobalJob(
                            unicId = runId,
                            descriptionRes = descriptionRes
                        )
                    )
                }
            }
            return block()
        } finally {
            if (descriptionRes != 0) {
                globalJobs.update { jobs ->
                    jobs.filter { it.unicId != runId }
                }
            }
        }
    }

}

data class GlobalJob(
    val unicId: Int = 0,
    val descriptionRes: Int = 0,
)