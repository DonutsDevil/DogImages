package com.example.network

import android.util.Log
import io.mockk.*
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import kotlin.math.log

open class BaseUnitTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            mockkStatic(Log::class)
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            unmockkAll()
        }
    }

    @Before
    open fun beforeEachTest() {
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.v(any(), any()) } returns 0
    }

    @After
    open fun afterEachTest() {
        clearAllMocks()
    }
}