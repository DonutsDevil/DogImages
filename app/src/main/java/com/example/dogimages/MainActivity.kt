package com.example.dogimages

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.network.ImageInit
import com.example.network.interfaces.Callback
import com.example.network.utility.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button
    private lateinit var etNumbers: EditText
    private lateinit var ivImage: ImageView
    private lateinit var rrProgressBar: RelativeLayout

    companion object {
        private const val TAG = "MainActivity"
    }

    private val dogImageProvider: ImageInit by lazy {
        ImageInit.getInstance(dogImageCallback())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        // Load an image before
        dogImageProvider.getNextImage()
    }


    override fun onResume() {
        super.onResume()

        btnNext.setOnClickListener { dogImageProvider.getNextImage() }

        btnPrevious.setOnClickListener {
            val bitmap = dogImageProvider.getPreviousImage()
            bitmap?.let { _bitmap ->
                Log.d(TAG, "onResume: setting previous image ${Thread.currentThread().name}")
                ivImage.setImageBitmap(_bitmap)
            } ?: Log.w(
                TAG,
                "onResume.setOnClickListener: bitmapImage is not available for this call"
            )
        }

        etNumbers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.trim()?.isEmpty() == true) {
                    btnSubmit.visibility = View.GONE
                } else {
                    try {
                        val numberInChar = s as CharSequence
                        val number = numberInChar.toString().toInt()
                        Log.d(TAG, "afterTextChanged: $number")
                        if (number <= 0) {
                            btnSubmit.visibility = View.GONE
                            etNumbers.error = "Number should be greater then 0"
                        } else {
                            btnSubmit.visibility = View.VISIBLE
                            etNumbers.error = null
                        }
                    } catch (e: NumberFormatException) {
                        etNumbers.error = "Number should be greater then 0 and non decimal"
                        btnSubmit.visibility = View.GONE
                    }
                }
            }
        })

        btnSubmit.setOnClickListener {
            dogImageProvider.getImages(etNumbers.text.toString().toInt())
            etNumbers.text = null
        }

    }

    private fun initViews() {
        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
        btnSubmit = findViewById(R.id.btn_submit)

        etNumbers = findViewById(R.id.et_number)

        ivImage = findViewById(R.id.iv_dog)

        rrProgressBar = findViewById(R.id.rr_progressBar)
    }

    /**
     * set visibility of the progress with ability to allows touch on screens
     */
    private fun setProgressBarVisibility(visibility: Int) {
        if (visibility == View.VISIBLE) {
            setScreenTouchRestriction()
        } else if (visibility == View.GONE) {
            clearScreenTouchRestriction()
        }
        rrProgressBar.visibility = visibility
    }

    /**
     * Allows user to interact with the screen again
     */
    private fun clearScreenTouchRestriction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    /**
     * Disable user to interact with the screen when progress bar is shown
     */
    private fun setScreenTouchRestriction() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private fun dogImageCallback() = object : Callback() {
        override fun onCompletion(state: Utility.Companion.UI_STATES, bitmap: Bitmap?) {
            val methodTag = "dogImageCallback.onCompletion"
            when (state) {
                Utility.Companion.UI_STATES.IN_PROGRESS -> {
                    Log.w(TAG, "$methodTag.IN_PROGRESS: Making server call")
                    setProgressBarVisibility(View.VISIBLE)
                }
                Utility.Companion.UI_STATES.FAILURE -> {
                    Log.w(TAG, "$methodTag.FAILURE: bitmapImage is not available for this call")
                    setProgressBarVisibility(View.GONE)

                }
                Utility.Companion.UI_STATES.DONE -> {
                    runOnUiThread {
                        bitmap?.let { _bitmap ->
                            ivImage.setImageBitmap(_bitmap)
                        } ?: Log.w(TAG, "$methodTag.DONE: bitmapImage is not available for this call")
                        setProgressBarVisibility(View.GONE)
                        btnPrevious.isEnabled = true
                    }
                }
                Utility.Companion.UI_STATES.IS_FIRST_IMAGE -> {
                    Log.w(TAG, "$methodTag.IS_FIRST_IMAGE: disabling previous button")
                    runOnUiThread {
                        btnPrevious.isEnabled = false
                    }
                }
            }

        }
    }
}