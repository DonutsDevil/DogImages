package com.example.network.networkManager

import com.example.network.interfaces.IResult
import com.example.network.utility.Executor

object NetworkManager {

    /**
     * Get List of size times which contains dog image bitmap
     * @param times should be greater then 0, Default is 1
     */
    internal fun fetchImages(times: Int = 1, callback: IResult) {
        Executor.submit {
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