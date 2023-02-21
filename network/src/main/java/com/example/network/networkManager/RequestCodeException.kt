package com.example.network.networkManager

import java.io.IOException

/**
 * Exception which notifies what is the RequestCode of the call
 * NOTE: This exception won't be called for 200 Request Code
 */
class RequestCodeException(val code: Int, message: String): IOException(message)

