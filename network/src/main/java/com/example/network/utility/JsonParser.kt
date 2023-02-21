package com.example.network.utility

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class JsonParser {
    companion object {
        private const val TAG = "JsonParser"

        /**
         * Retrieve image from the json string
         * @param json is a String which contains the json response after making the API call
         * @return String, The url of the image from the json
         * @throws IOException if any exception occurs
         */
        @Throws(JSONException::class)
        fun getImageUrl(json: String): String? {
            if (json.isEmpty()) {
                Log.d(TAG, "getImageBitmap: jsonResponse is empty")
                return null
            }
            val baseJsonObject = JSONObject(json)
            return baseJsonObject.getString("message")
        }
    }
}