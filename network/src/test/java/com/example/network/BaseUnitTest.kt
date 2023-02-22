package com.example.network

import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass

open class BaseUnitTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {

        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            unmockkAll()
        }
    }

    @Before
    open fun beforeEachTest() {

    }

    @After
    open fun afterEachTest() {
        clearAllMocks()
    }
}