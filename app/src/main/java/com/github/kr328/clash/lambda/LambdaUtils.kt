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

const val ServerAddressUrlCacheKey = "server_address_url_cache_key"
suspend fun fetchServerUrlFromLambda(context: Context): String = withContext(Dispatchers.IO) {
    val cacheUtil = CacheUtil(context)
    val cache = cacheUtil.getCachedString(ServerAddressUrlCacheKey)
    if (cache != null) {
        return@withContext cache
    }

    val url = URL("https://3sm6xoow37.execute-api.us-east-2.amazonaws.com/airport_server_url")
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            reader.use {
                val url = it.readText()
                cacheUtil.cacheString(ServerAddressUrlCacheKey, url)
                url
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