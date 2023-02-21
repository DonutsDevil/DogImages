package com.example.network.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream


/**
 * @return bitmap from the @param imageInputStream
 * inputStream is closed by so this is won't cause a leak
 */
fun extractImage(imageInputStream: InputStream): Bitmap? {
    val bitmap: Bitmap?
    imageInputStream.use { _imageInputStream ->
        bitmap = BitmapFactory.decodeStream(_imageInputStream)
    }
    return bitmap
}