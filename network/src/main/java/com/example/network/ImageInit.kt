package com.example.network

import android.graphics.Bitmap
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
     */
    fun getImage() {
        val imageList = NetworkManager.fetchImages()
        if (imageList.isNotEmpty()) {
            imagesBitmapList.add(imageList[0])
        }
    }

    /**
     * gets [number] of bitmaps from the server and adds to the list
     */
    fun getImages(number: Int) {
        val imageList = NetworkManager.fetchImages(number)
        if (imageList.isNotEmpty()) {
            imagesBitmapList.addAll(imageList)
        }
    }

    /**
     * Shows the next image, If the user is at the end then it will fetch the image [getImage] and then show
     * @return Bitmap of the next image which is to be showed
     */
    fun getNextImage(): Bitmap? {
        if (getCurrentListIndex() == imagesBitmapList.size) {
            // User is currently at the end of the list
            // fetch a image and return the same
            getImage()
        }
        val bitmap = imagesBitmapList[getCurrentListIndex()]
        incrementUserViewIndex()
        return  bitmap
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