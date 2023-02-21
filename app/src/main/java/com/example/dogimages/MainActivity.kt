package com.example.dogimages

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.network.ImageInit
import com.example.network.interfaces.Callback

class MainActivity : AppCompatActivity() {

    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button
    private lateinit var etNumbers: EditText
    private lateinit var ivImage: ImageView

    companion object {
        private const val TAG = "MainActivity"
    }

    private val dogImageProvider: ImageInit by lazy {
        ImageInit.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }


    override fun onResume() {
        super.onResume()

        btnNext.setOnClickListener {
            dogImageProvider.getNextImage(object : Callback() {
                val methodTag = "onResume.getNextImage"
                override fun onSuccess(bitmap: Bitmap?) {
                    bitmap?.let { _bitmap ->
                        runOnUiThread {
                            ivImage.setImageBitmap(_bitmap)
                        }
                    } ?: Log.w(TAG, "$methodTag.onSuccess: bitmapImage is not available for this call")
                }

                override fun onFailure() {
                    Log.w(TAG, "$methodTag.onFailure: bitmapImage is not available for this call")
                }
            })
        }

        btnPrevious.setOnClickListener {
            val bitmap = dogImageProvider.getPreviousImage()
            bitmap?.let { _bitmap ->
                Log.d(TAG, "onResume: setting previous image ${Thread.currentThread().name}")
                ivImage.setImageBitmap(_bitmap)
            } ?: Log.w(TAG, "onResume.setOnClickListener: bitmapImage is not available for this call")
        }

    }

    private fun initViews() {
        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
        btnSubmit = findViewById(R.id.btn_submit)

        etNumbers = findViewById(R.id.et_number)

        ivImage = findViewById(R.id.iv_dog)
    }

}