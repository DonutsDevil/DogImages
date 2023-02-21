package com.example.network.networkManager

import android.graphics.Bitmap

object NetworkManager {
    private const val TAG = "NetworkManager"

    /**
     * Get List of size times which contains dog image bitmap
     * @param times should be greater then 0, Default is 1
     */
    fun fetchImages(times: Int = 1): List<Bitmap?> {
        return getRetryInterceptor().makeHttpCall(times)
    }

    /**
     * @return [RetryInterceptor]
     */
    private fun getRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor(HttpCall())
    }
}