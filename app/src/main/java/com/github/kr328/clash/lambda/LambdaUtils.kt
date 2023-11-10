package com.github.kr328.clash.lambda

import android.content.Context
import android.content.SharedPreferences
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
suspend fun fetchStringUrlFromLambda(context: Context, urlString: String): String = withContext(Dispatchers.IO) {
    val cacheUtil = CacheUtil(context)
    val cache = cacheUtil.getCachedString(urlString)
    if (cache != null) {
        return@withContext cache
    }

    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            reader.use {
                val urlFromResponse = it.readText()
                cacheUtil.cacheString(urlString, urlFromResponse)
                urlFromResponse
            }
        } else {
            "Error: $responseCode"
        }
    } finally {
        connection.disconnect()
    }
}


class CacheUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("CachePrefs", Context.MODE_PRIVATE)

    fun cacheString(key: String, value: String) {
        val currentTime = System.currentTimeMillis()
        prefs.edit()
            .putString(key, value)
            .putLong("${key}_time", currentTime)
            .apply()
    }

    fun getCachedString(key: String): String? {
        val cachedTime = prefs.getLong("${key}_time", 0)
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - cachedTime

        if (diff < TimeUnit.DAYS.toMillis(1)) {
            return prefs.getString(key, null)
        }

        return null
    }
}