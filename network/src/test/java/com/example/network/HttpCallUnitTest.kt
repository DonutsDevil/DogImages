package com.example.network

import com.example.network.networkManager.HttpCall
import com.example.network.networkManager.RequestCodeException
import io.mockk.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection

class HttpCallUnitTest : BaseUnitTest() {

    companion object {

        private var httpCallSpy: HttpCall? = null
        private const val prefectUrl = "https://dog.ceo/api/breeds/image/random"
        private const val malformedUrl = "htts://dog.ceo/api/breeds/image/random"
        private const val validJsonResponse =
            "{\"message\":\"https:\\/\\/images.dog.ceo\\/breeds\\/dingo\\/n02115641_136.jpg\",\"status\":\"success\"}"

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            BaseUnitTest.beforeClass()
            httpCallSpy = spyk()

        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            BaseUnitTest.afterClass()
            httpCallSpy = null
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun fetchJson_when_request_url_is_Empty() {
        // setup
        val emptyUrl = ""

        // invoke
        val json = httpCallSpy!!.fetchJson(emptyUrl)

        // verify
        Assert.assertEquals("", json)
        verify(exactly = 0) { httpCallSpy!!.createUrl(emptyUrl) }
    }

    @Test(expected = MalformedURLException::class)
    fun fetchJson_when_malformed_url_is_passed() {
        // invoke
        httpCallSpy!!.fetchJson(malformedUrl)

        // verify
        verify(exactly = 0) { httpCallSpy!!.makeHttpRequest(any()) }
        verify(exactly = 1) { httpCallSpy!!.createUrl(malformedUrl) }
    }

    @Test()
    fun fetchJson_when_makeHttpRequest_returns_null_inputStream() {
        // setup
        val URLMock = mockk<URL>()
        every { httpCallSpy!!.createUrl(prefectUrl) } returns URLMock
        every { httpCallSpy!!.makeHttpRequest(URLMock) } returns null

        // invoke
        val json = httpCallSpy!!.fetchJson(prefectUrl)

        // verify
        Assert.assertEquals("", json)
        verify(exactly = 1) { httpCallSpy!!.makeHttpRequest(URLMock) }
        verify(exactly = 1) { httpCallSpy!!.createUrl(prefectUrl) }
        verify(exactly = 0) { httpCallSpy!!.readFromStream(any()) }
    }

    @Test()
    fun fetchJson_when_makeHttpRequest_returns_non_null_inputStream() {
        // setup
        val URLMock = mockk<URL>()
        val inputStreamMock = mockk<InputStream>()
        every { httpCallSpy!!.createUrl(prefectUrl) } returns URLMock
        every { httpCallSpy!!.makeHttpRequest(URLMock) } returns inputStreamMock
        every { httpCallSpy!!.readFromStream(inputStreamMock) } returns validJsonResponse
        every { inputStreamMock.close() } returns Unit

        // invoke
        val json = httpCallSpy!!.fetchJson(prefectUrl)

        // verify
        Assert.assertEquals(validJsonResponse, json)
        verify(exactly = 1) { httpCallSpy!!.makeHttpRequest(URLMock) }
        verify(exactly = 1) { httpCallSpy!!.createUrl(prefectUrl) }
        verify(exactly = 1) { httpCallSpy!!.readFromStream(inputStreamMock) }
        verify(exactly = 1) { inputStreamMock.close() }
    }

    @Test(expected = RequestCodeException::class)
    fun fetchJson_when_makeHttpRequest_throws_RequestCodeException() {
        // setup
        val URLMock = mockk<URL>()
        val inputStreamMock = mockk<InputStream>()
        every { httpCallSpy!!.createUrl(prefectUrl) } returns URLMock
        every { httpCallSpy!!.makeHttpRequest(URLMock) } throws RequestCodeException(300, "test")

        // invoke
        httpCallSpy!!.fetchJson(prefectUrl)

        // verify
        verify(exactly = 1) { httpCallSpy!!.makeHttpRequest(URLMock) }
        verify(exactly = 1) { httpCallSpy!!.createUrl(prefectUrl) }
        verify(exactly = 0) { httpCallSpy!!.readFromStream(inputStreamMock) }
    }

    @Test(expected = MalformedURLException::class)
    fun createUrl_requestUrl_is_invalid() {
        //invoke
        httpCallSpy!!.createUrl(malformedUrl)
    }

    @Test
    fun makeHttpRequest_when_response_is_200() {
        // setup
        val httpUrlConnectionMock = mockk<HttpURLConnection>()
        val inputStreamMock = mockk<InputStream>()
        val urlMock = mockk<URL>()
        val urlConnectionMock = mockk<URLConnection>()

        every { urlMock.openConnection() } returns urlConnectionMock
        every { httpUrlConnectionMock.responseCode } returns 200
        every { httpUrlConnectionMock.inputStream } returns inputStreamMock
        every { httpUrlConnectionMock.readTimeout = 10000 /* milliseconds */ } returns Unit
        every { httpUrlConnectionMock.connectTimeout = 15000 /* milliseconds */ } returns Unit
        every { httpUrlConnectionMock.requestMethod = "GET" } returns Unit
        every { httpUrlConnectionMock.connect() } returns Unit
        every { urlMock.openConnection() as HttpURLConnection } returns httpUrlConnectionMock
        every { httpUrlConnectionMock.disconnect() } returns Unit

        // invoke
        val inputStream = httpCallSpy!!.makeHttpRequest(urlMock)

        // verify
        Assert.assertNotNull(inputStream)
        verify(exactly = 1) { httpUrlConnectionMock.disconnect() }
    }

    @Test(expected = IOException::class)
    fun makeHttpRequest_when_open_connection_throws_IOException() {
        // setup
        val httpUrlConnectionMock = mockk<HttpURLConnection>()
        val urlMock = mockk<URL>()
        every { urlMock.openConnection() } throws IOException()

        // invoke
        val inputStream = httpCallSpy!!.makeHttpRequest(urlMock)

        // verify
        Assert.assertNull(inputStream)
        verify(exactly = 0) { httpUrlConnectionMock.disconnect() }
    }

    @Test(expected = RequestCodeException::class)
    fun makeHttpRequest_when_response_is_not_200() {
        // setup
        val httpUrlConnectionMock = mockk<HttpURLConnection>()
        val urlMock = mockk<URL>()
        val urlConnectionMock = mockk<URLConnection>()

        every { urlMock.openConnection() } returns urlConnectionMock
        every { httpUrlConnectionMock.responseCode } returns 300
        every { httpUrlConnectionMock.readTimeout = 10000 /* milliseconds */ } returns Unit
        every { httpUrlConnectionMock.connectTimeout = 15000 /* milliseconds */ } returns Unit
        every { httpUrlConnectionMock.requestMethod = "GET" } returns Unit
        every { httpUrlConnectionMock.responseMessage } returns ""
        every { httpUrlConnectionMock.connect() } returns Unit
        every { urlMock.openConnection() as HttpURLConnection } returns httpUrlConnectionMock
        every { httpUrlConnectionMock.disconnect() } returns Unit

        // invoke
        httpCallSpy!!.makeHttpRequest(urlMock)

        // verify
        verify(exactly = 1) { httpUrlConnectionMock.disconnect() }
    }

    @Test
    fun readFromStream_when_inputStream_parameter_is_null() {
        // invoke
        val jsonString = httpCallSpy!!.readFromStream(null)

        // verify
        Assert.assertEquals(jsonString, "")
    }
}