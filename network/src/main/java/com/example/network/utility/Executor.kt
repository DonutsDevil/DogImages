package com.example.network.utility

import java.util.concurrent.Future
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Class which is wrapper for accessing [ThreadPoolExecutor]
 */
object Executor {

    private var executor: ThreadPoolExecutor? = ThreadPoolExecutor(
        0, Int.MAX_VALUE,
        30L, TimeUnit.SECONDS,
        SynchronousQueue()
    )

    /**
     * submit [runnable] to the [ThreadPoolExecutor]
     */
    fun submit(runnable: Runnable): Future<*>? {
        return executor?.submit(runnable)
    }

}