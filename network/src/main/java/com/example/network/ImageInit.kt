package com.example.network

import android.graphics.Bitmap
import android.util.Log
import com.example.network.interfaces.Callback
import com.example.network.interfaces.IResult
import com.example.network.networkManager.NetworkManager

class ImageInit(private val callback: Callback) {

    companion object {
        private val imagesBitmapList = mutableListOf<Bitmap?>()
        private const val TAG = "ImageInit"

        /**
         * this index shows the current place where the user is in the imageBitmapList
         */
        @Volatile
        private var currentListIndex = -1
        private val lock = Any()

        @Volatile
        private var instance: ImageInit? = null

        fun getInstance(callback: Callback): ImageInit {
            if (instance == null) {
                synchronized(lock) {
                    if (instance == null) {
                        instance = ImageInit(callback)
                    }
                }
            }
            return instance!!
        }
    }

    /**
     * get a image from the server and adds to the list
     * @return true if addition of bitmap was success else false
     */
    private fun getImage(successRunnable: Runnable, failureRunnable: Runnable) {
        NetworkManager.fetchImages(1, object : IResult {
            override fun onResponse(bitmapList: List<Bitmap?>) {
                if (bitmapList.isNotEmpty()) {
                    synchronized(lock) {
                        imagesBitmapList.add(bitmapList[0])
                        successRunnable.run()
                    }
                } else {
                    failureRunnable.run()
                }
            }
        })
    }

    /**
     * gets [number] of bitmaps from the server and adds to the list
     */
    fun getImages(number: Int) {
        callback.inProgress()
        NetworkManager.fetchImages(number, object : IResult {
            override fun onResponse(bitmapList: List<Bitmap?>) {
                if (bitmapList.isNotEmpty()) {
                    synchronized(lock) {
                        imagesBitmapList.addAll(bitmapList)
                        callback.onSuccess(null)
                    }
                } else {
                    callback.onFailure()
                }
                isPreviousImageAvailable()
            }
        })

    }

    /**
     * Shows the next image, If the user is at the end then it will fetch the image [getImage] and then show
     * @return Bitmap of the next image which is to be showed
     */
    fun getNextImage() {
        callback.inProgress()
        if (getCurrentListIndex() + 1 == imagesBitmapList.size) {
            Log.d(TAG, "getNextImage: Fetch new image")
            // User is currently at the end of the list
            // fetch a image and return the same
            val failureRunnable = Runnable {
                callback.onFailure()
            }
            getImage(getNextImageSuccessRunnable(callback), failureRunnable)
        } else {
            Log.d(TAG, "getNextImage: Use loaded image")
            getNextImageSuccessRunnable(callback).run()
        }
    }

    /**
     * @return runnable which needs to be run when we want to [getNextImage]
     */
    private fun getNextImageSuccessRunnable(callback: Callback): Runnable {
        return Runnable {
            incrementUserViewIndex()
            val bitmap = imagesBitmapList[getCurrentListIndex()]
            callback.onSuccess(bitmap)
            isPreviousImageAvailable()
        }
    }

    /**
     * Shows the previous image where the user was before viewing the [getCurrentListIndex] image
     * @return Bitmap of the previous image
     */
    fun getPreviousImage(): Bitmap? {
        if (getCurrentListIndex() <= 0 || imagesBitmapList.isEmpty()) {
            // do nothing since there is no image to be shown
            Log.i(TAG, "getPreviousImage: do nothing since there is no image to be shown")
            isPreviousImageAvailable()
            return null
        }
        decrementUserViewIndex()
        isPreviousImageAvailable()
        return imagesBitmapList[getCurrentListIndex()]
    }

    /**
     * Tells whether theres any dog image before the current image that is been shown on the screen
     */
    private fun isPreviousImageAvailable() {
        if (getCurrentListIndex() <= 0) {
            Log.d(TAG, "isPreviousImageAvailable: ${getCurrentListIndex()}")
            // means there are no bitmap image before this
            callback.isFirstImage()
        }
    }


    private fun incrementUserViewIndex() {
        currentListIndex++
    }

    private fun decrementUserViewIndex() {
        currentListIndex--
    }

    private fun getCurrentListIndex(): Int {
        return currentListIndex
    }
}