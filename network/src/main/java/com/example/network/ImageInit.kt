package com.example.network

import android.graphics.Bitmap
import com.example.network.interfaces.Callback
import com.example.network.interfaces.IResult
import com.example.network.networkManager.NetworkManager

class ImageInit {

    companion object {
        private val imagesBitmapList = mutableListOf<Bitmap?>()

        /**
         * this index shows the current place where the user is in the imageBitmapList
         */
        private var currentListIndex = 0
        private val lock = Any()

        @Volatile
        private lateinit var instance: ImageInit

        fun getInstance(): ImageInit {
            if (instance == null) {
                synchronized(lock) {
                    if (instance == null) {
                        instance = ImageInit()
                    }
                }
            }
            return instance
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
                    }
                    successRunnable.run()
                } else {
                    failureRunnable.run()
                }
            }
        })
    }

    /**
     * gets [number] of bitmaps from the server and adds to the list
     */
    fun getImages(number: Int, callback: Callback) {
        NetworkManager.fetchImages(number, object : IResult {
            override fun onResponse(bitmapList: List<Bitmap?>) {
                val imageAddedToTheList = if (bitmapList.isNotEmpty()) {
                    synchronized(lock) {
                        imagesBitmapList.addAll(bitmapList)
                    }
                } else {
                    false
                }
                callback.onCompletion(imageAddedToTheList, null)
            }
        })

    }

    /**
     * Shows the next image, If the user is at the end then it will fetch the image [getImage] and then show
     * @return Bitmap of the next image which is to be showed
     */
    fun getNextImage(callback: Callback) {
        if (getCurrentListIndex() == imagesBitmapList.size) {
            // User is currently at the end of the list
            // fetch a image and return the same
            val failureRunnable = Runnable {
                callback.onCompletion(false, null)
            }
            getImage(getNextImageSuccessRunnable(callback), failureRunnable)
        } else {
            getNextImageSuccessRunnable(callback).run()
        }
    }

    /**
     * @return runnable which needs to be run when we want to [getNextImage]
     */
    private fun getNextImageSuccessRunnable(callback: Callback): Runnable {
        return Runnable {
            val bitmap = imagesBitmapList[getCurrentListIndex()]
            callback.onCompletion(true, bitmap)
            incrementUserViewIndex()
        }
    }

    /**
     * Shows the previous image where the user was before viewing the [getCurrentListIndex] image
     * @return Bitmap of the previous image
     */
    fun getPreviousImage(): Bitmap? {
        if (getCurrentListIndex() <= 0 || imagesBitmapList.isEmpty()) {
            // do nothing since there is no image to be shown
            return null
        }
        decrementUserViewIndex()
        return imagesBitmapList[getCurrentListIndex()]
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