package com.example.network.interfaces

import android.graphics.Bitmap

internal interface IResult {
    fun onResponse(bitmapList: List<Bitmap?>)
}