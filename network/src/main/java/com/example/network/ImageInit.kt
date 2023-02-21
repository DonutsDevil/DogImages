package com.example.network

import android.graphics.Bitmap
import com.example.network.interfaces.Callback
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
    private fun getImage(): Boolean {
        val imageList = NetworkManager.fetchImages()
        return if (imageList.isNotEmpty()) {
            imagesBitmapList.add(imageList[0])
        } else {
            false
        }
    }

    /**
     * gets [number] of bitmaps from the server and adds to the list
     */
    fun getImages(number: Int, callback: Callback) {
        val imageList = NetworkManager.fetchImages(number)
        val imageAddedToTheList = if (imageList.isNotEmpty()) {
            imagesBitmapList.addAll(imageList)
        } else {
            false
        }
        callback.onCompletion(imageAddedToTheList, null)
    }

    /**
     * Shows the next image, If the user is at the end then it will fetch the image [getImage] and then show
     * @return Bitmap of the next image which is to be showed
     */
    fun getNextImage(callback: Callback) {
        if (getCurrentListIndex() == imagesBitmapList.size) {
            // User is currently at the end of the list
            // fetch a image and return the same
            val isImageFetched = getImage()
            if (isImageFetched) {
                val bitmap = imagesBitmapList[getCurrentListIndex()]
                callback.onCompletion(true, bitmap)
                incrementUserViewIndex()
            } else {
                callback.onCompletion(false, null)
            }
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