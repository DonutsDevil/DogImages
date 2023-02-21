package com.example.network.interfaces

import android.graphics.Bitmap

private interface ICallback {
    /**
     * bitmap can be null in case where library as added n number of images in the list
     * i.e when calling [com.example.network.ImageInit.getImages]
     * OR when status is false
     */
    fun onCompletion(status: Boolean, bitmap: Bitmap?)
}


abstract class Callback: ICallback {
    /**
     * Depending on [status] it will call on failure or success
     */
    override fun onCompletion(status: Boolean, bitmap: Bitmap?) {
        if (status) {
            onSuccess(bitmap)
        } else {
            onFailure()
        }
    }
    protected abstract fun onSuccess(bitmap: Bitmap?)
    protected abstract fun onFailure()
}