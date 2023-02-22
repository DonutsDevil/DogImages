package com.example.network.networkManager

import com.example.network.interfaces.IResult
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object NetworkManager {

    val executor by lazy {
        ThreadPoolExecutor(
            0, Int.MAX_VALUE,
            30L, TimeUnit.SECONDS,
            SynchronousQueue()
        )
    }

    /**
     * Get List of size times which contains dog image bitmap
     * @param times should be greater then 0, Default is 1
     */
    internal fun fetchImages(times: Int = 1, callback: IResult) {
        executor.submit {
            geHttpCallHandler().makeHttpCall(times, callback)
        }
    }

    /**
     * @return [HttpCallHandler]
     */
    private fun geHttpCallHandler(): HttpCallHandler {
        return HttpCallHandler(HttpCall())
    }
}