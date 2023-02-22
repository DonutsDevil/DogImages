package com.example.network.networkManager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.network.utility.JsonParser
import org.json.JSONException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketException
import java.net.UnknownHostException
import kotlin.random.Random

/**
 * Class is responsible for making calls to the server and also make retry calls if response code is not 200
 * [times_retry] means that 1st call is the default call then if any failure we do +2 retry call
 */
class RetryInterceptor(private val httpCall: HttpCall) {

    companion object {
        private const val TAG = "RetryInterceptor"
        private const val times_retry = 3
        private const val url = "https://dog.ceo/api/breeds/image/random"
        private val signal_lock = Object()// signal lock
        private val addLock = Object()
    }

    /**
     * Makes the times server call and returns a list of bitmap as a result
     * List of bitmap will be equal to the times the call is made to the server
     * @param times number of times call is to be made
     * @return list of bitmaps which we receive after making @times server calls
     */
    fun makeHttpCall(times: Int): List<Bitmap?> {
        if (times <= 0) {
            Log.d(
                TAG,
                "makeHttpCall: number of time to fetch dog image is less or equal to 0: $times"
            )
            return emptyList()
        }
        val imageList = mutableListOf<Bitmap?>()
        for (makeCall in 1..times) {
            Log.d(TAG, "makeHttpCall: before making submit")
            NetworkManager.executor.submit {
                Log.d(TAG, "makeHttpCall: call $makeCall")
                val bitmap = fetchBitmap()
                synchronized(addLock) {
                    imageList.add(bitmap)
                    if (imageList.size == times) {
                        Log.d(TAG, "makeHttpCall: is notify the lock")
                        synchronized(signal_lock) {
                            signal_lock.notifyAll()
                        }
                    }
                }
                Log.d(TAG, "makeHttpCall: is after sync block")
            }
        }
        Log.d(TAG, "makeHttpCall: going to wait")
        synchronized(signal_lock) {
            signal_lock.wait()
        }
        Log.d(TAG, "makeHttpCall: passing the list $imageList")
        return imageList
    }

    /**
     * method use to make calls, It handles the retry mechanism if any exception or response is not 200
     * @param retry is [times_retry] by default
     * @return bitmap which we receive after making the call
     */
    private fun fetchBitmap(retry: Int = times_retry): Bitmap? {
        if (retry == 0) {
            // We exhausted the retries hence returning null
            return null
        } else if (retry != 3) {
            sleep() // this means we are retrying, So lets retry after some delay
        }
        return try {
            val jsonResponse = httpCall.fetchJson(url)
            val imageUrl = JsonParser.getImageUrl(jsonResponse)
            return imageUrl?.let { _imageUrl ->
                val `in` = java.net.URL(_imageUrl).openStream()
                BitmapFactory.decodeStream(`in`)
            }
        } catch (u: UnknownHostException) {
            Log.w(TAG, "fetchBitmap: internet is not connected", u)
            fetchBitmap(0) // force it out since internet is not connected
        } catch (r: RequestCodeException) {
            Log.e(TAG, "fetchBitmap: Call failed: Response code: ${r.code}", r)
            fetchBitmap(retry - 1)
        } catch (f: FileNotFoundException) {
            Log.w(TAG, "fetchBitmap: file not found for image", f)
            fetchBitmap(retry - 1)
        } catch (s: SocketException) {
            Log.e(TAG, "fetchBitmap: Socket timeout", s)
            fetchBitmap(retry - 1)
        } catch (i: IOException) {
            Log.e(TAG, "fetchBitmap: Unexpected failure happened", i)
            fetchBitmap(retry - 1)
        } catch (j: JSONException) {
            Log.e(TAG, "fetchBitmap: invalid json while parsing", j)
            fetchBitmap(0) // force it out since json is invalid
        }
    }

    /**
     * Sleep before making next call to the server
     * Sleep time can vary from 100 millisecond to 1000 millisecond
     */
    private fun sleep() {
        try {
            Thread.sleep((100 + Random.nextLong(100, 900)))
        } catch (e: InterruptedException) {
            Log.w(TAG, "sleep: thread interrupted")
        }
    }
}