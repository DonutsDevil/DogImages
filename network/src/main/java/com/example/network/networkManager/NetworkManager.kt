package com.example.network.networkManager

import com.example.network.interfaces.IResult
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object NetworkManager {
    private const val TAG = "NetworkManager"

    private val executor by lazy {
        ThreadPoolExecutor(0, 4,
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
            val imageBitmapList = getRetryInterceptor().makeHttpCall(times)
            callback.onResponse(imageBitmapList)
        }
    }

    /**
     * @return [RetryInterceptor]
     */
    private fun getRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor(HttpCall())
    }
}