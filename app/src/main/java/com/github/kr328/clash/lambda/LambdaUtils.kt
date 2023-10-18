package com.github.kr328.clash.lambda

import com.github.kr328.clash.common.log.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun fetchServerUrlFromLambda(): String = withContext(Dispatchers.IO) {
    val url = URL("https://3sm6xoow37.execute-api.us-east-2.amazonaws.com/airport_server_url")
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            reader.use {
                it.readText()
            }
        } else {
            "Error: $responseCode"
        }
    } finally {
        connection.disconnect()
    }
}