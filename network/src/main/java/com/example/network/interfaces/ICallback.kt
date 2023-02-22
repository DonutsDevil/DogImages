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
     * passes failure reason.
     */
    fun onFailure(reason: String)

    /**
     * In the Begin of the server we mark it inProgress
     * always on main thread
     */
    fun inProgress()

    /**
     * If it's the first image, we call this
     */
    fun isFirstImage()
}


abstract class Callback : ICallback {
    /**
     * bitmap can be null in case where library as added n number of images in the list
     * i.e when calling [com.example.network.ImageInit.getImages],
     * when status other then [Utility.Companion.UI_STATES.DONE]
     * @param state [Utility.Companion.UI_STATES] defines states at which the request is
     * @param bitmap holds the fetched bitmap if [Utility.Companion.UI_STATES.DONE], else null
     * @param reason is empty by default, passes value as needed
     */
    protected abstract fun onCompletion(state: Utility.Companion.UI_STATES, bitmap: Bitmap?, reason: String = "")

    override fun onSuccess(bitmap: Bitmap?) {
        onCompletion(Utility.Companion.UI_STATES.DONE, bitmap)
    }

    override fun onFailure() {
        onCompletion(Utility.Companion.UI_STATES.FAILURE, null)
    }

    override fun inProgress() {
        onCompletion(Utility.Companion.UI_STATES.IN_PROGRESS, null)
    }

    override fun isFirstImage() {
        onCompletion(Utility.Companion.UI_STATES.IS_FIRST_IMAGE, null)
    }

    override fun onFailure(reason: String) {
        onCompletion(Utility.Companion.UI_STATES.FAILURE, null, reason)
    }

}