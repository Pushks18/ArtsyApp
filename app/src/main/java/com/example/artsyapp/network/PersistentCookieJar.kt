@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.artsyapp.network

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar(context: Context) : CookieJar {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    private var cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()

    init {
        prefs.getString("cookies", null)?.let { saved ->
            runCatching {
                val parsed = json.decodeFromString<Map<String, List<SerializableCookie>>>(saved)
                cookieStore = parsed.mapValues { it.value.map { cookie -> cookie.toCookie() } }.toMutableMap()
            }.onFailure { it.printStackTrace() }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
        saveCookies()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val validCookies = cookieStore[url.host].orEmpty().filter { it.expiresAt >= System.currentTimeMillis() }
        cookieStore[url.host] = validCookies
        saveCookies()
        return validCookies
    }

    private fun saveCookies() {
        val serialized = cookieStore.mapValues { it.value.map { cookie -> SerializableCookie.from(cookie) } }
        prefs.edit().putString("cookies", json.encodeToString(serialized)).apply()
    }

    @Serializable
    data class SerializableCookie(
        val name: String,
        val value: String,
        val expiresAt: Long,
        val domain: String,
        val path: String,
        val secure: Boolean,
        val httpOnly: Boolean
    ) {
        fun toCookie(): Cookie = Cookie.Builder()
            .name(name)
            .value(value)
            .expiresAt(expiresAt)
            .domain(domain)
            .path(path)
            .apply {
                if (secure) secure()
                if (httpOnly) httpOnly()
            }.build()

        companion object {
            fun from(cookie: Cookie): SerializableCookie = SerializableCookie(
                name = cookie.name,
                value = cookie.value,
                expiresAt = cookie.expiresAt,
                domain = cookie.domain,
                path = cookie.path,
                secure = cookie.secure,
                httpOnly = cookie.httpOnly
            )
        }
    }
}
