package com.example.network.networkManager

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

/**
 * Class is responsible to make the server call
 */
class HttpCall {
    companion object {
        private const val TAG = "HttpCall"
    }

    /**
     * Fetches data from the requestUrl and returns jsonResponse for the same.
     * @param requestUrl url with protocol to which HTTP call is to be made
     * @return jsonResponse of the requestUrl as String
     * @throws RequestCodeException if response code is is not 200
     * @throws MalformedURLException if no protocol is specified in requestUrl
     * @throws IllegalArgumentException if requestUrl is empty
     * @throws IOException if any exception occurred while reading or making connection
     */
    @Throws(Exception::class)
    fun fetchJson(requestUrl: String): String {
        if (requestUrl.isEmpty()) {
            throw IllegalArgumentException("Url is empty")
        }
        val url = createUrl(requestUrl)
        val inputStream = makeHttpRequest(url)
        var jsonResponse = ""
        inputStream.use { _inputStream ->
            jsonResponse = readFromStream(_inputStream)
        }
        return jsonResponse
    }

    /**
     * Fetches data from the requestUrl and returns InputStream for the same.
     * @param requestUrl url with protocol to which HTTP call is to be made
     * @return InputStream of the requestUrl
     * @throws RequestCodeException if response code is is not 200
     * @throws MalformedURLException if no protocol is specified in requestUrl
     * @throws IllegalArgumentException if requestUrl is empty
     * @throws IOException if any exception occurred while reading or making connection
     */
    @Throws(Exception::class)
    fun getInputStream(requestUrl: String): InputStream? {
        if (requestUrl.isEmpty()) {
            throw IllegalArgumentException("Url is empty")
        }
        val url = createUrl(requestUrl)
        return makeHttpRequest(url)
    }

    /**
     * Create URL object from the String url representation
     * @param requestUrl url which needs to be converted URL object
     * @return URL object of @param requestUrl
     * @throws MalformedURLException if no protocol is specified, or an unknown protocol is found, or spec is null.
     */
    @Throws(MalformedURLException::class)
    private fun createUrl(requestUrl: String): URL {
        return URL(requestUrl)
    }

    /**
     * makes http call to the @param url
     * @param url url where the call is to be made.
     * @return jsonResponse as String which we get after make call to @param url
     * @throws IOException if closing of the InputStream caused any issue or the response code was not 200.
     */
    @Throws(IOException::class)
    private fun makeHttpRequest(url: URL): InputStream? {
        var urlConnection: HttpURLConnection? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000 /* milliseconds */
            urlConnection.connectTimeout = 15000 /* milliseconds */
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            return if (urlConnection.responseCode == 200) {
                urlConnection.inputStream
            } else {
                throw RequestCodeException(
                    urlConnection.responseCode,
                    urlConnection.responseMessage
                        ?: "Retry the call response code: ${urlConnection.responseCode}"
                )
            }
        } finally {
            urlConnection?.disconnect()
        }
    }

    /**
     * Reads from the inputStream and returns json response as string from the inputStream that is provided
     * @param inputStream which which contains the json after making the http call
     * @return String jsonResponse got from inputStream.
     * @throws IOException if any exception occurs while reading from the inputStream
     */
    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        inputStream?.let { _inputStream ->
            val inputStreamReader = InputStreamReader(_inputStream, Charset.forName("UTF-8"))
            val bufferedReader = BufferedReader(inputStreamReader)
            var line = bufferedReader.readLine()
            while (line != null) {
                output.append(line)
                line = bufferedReader.readLine()
            }
            Log.d(TAG, "readFromStream: json response from the inputStream = $output")
            return output.toString()
        } ?: Log.e(TAG, "readFromStream: InputStream is null")
        return ""
    }
}