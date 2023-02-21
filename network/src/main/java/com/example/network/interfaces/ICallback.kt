package com.example.network.interfaces

import android.graphics.Bitmap
import com.example.network.utility.Utility

private interface ICallback {
    /**
     * After fetching the image from the server
     * @param bitmap image which is fetched
     */
    fun onSuccess(bitmap: Bitmap?)

    /**
     * Any failure / Exception when making the server call to fetch image
     */
    fun onFailure()

    /**
     * In the Begin of the server we mark it inProgress
     */
    fun inProgress()
}


abstract class Callback : ICallback {
    /**
     * bitmap can be null in case where library as added n number of images in the list
     * i.e when calling [com.example.network.ImageInit.getImages],
     * when status is [Utility.Companion.UI_STATES.FAILURE] / [Utility.Companion.UI_STATES.IN_PROGRESS]
     * @param state [Utility.Companion.UI_STATES] defines states at which the request is
     * @param bitmap holds the fetched bitmap if [Utility.Companion.UI_STATES.DONE], else null
     */
    protected abstract fun onCompletion(state: Utility.Companion.UI_STATES, bitmap: Bitmap?)

    override fun onSuccess(bitmap: Bitmap?) {
        onCompletion(Utility.Companion.UI_STATES.DONE, bitmap)
    }

    override fun onFailure() {
        onCompletion(Utility.Companion.UI_STATES.FAILURE, null)
    }

    override fun inProgress() {
        onCompletion(Utility.Companion.UI_STATES.IN_PROGRESS, null)
    }

}