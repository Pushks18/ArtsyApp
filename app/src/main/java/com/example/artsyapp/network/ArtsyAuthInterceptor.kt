package com.example.artsyapp.network

import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log

class ArtsyAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val artsyToken = TokenManager.artsyToken
        val jwtToken = TokenManager.jwtToken

        val requestBuilder = chain.request().newBuilder()

        val cookieHeader = buildString {
            if (!artsyToken.isNullOrBlank()) append("artsyToken=$artsyToken; ")
            if (!jwtToken.isNullOrBlank()) append("jwtToken=$jwtToken;")
        }.trim()

        if (cookieHeader.isNotEmpty()) {
            requestBuilder.addHeader("Cookie", cookieHeader)
            Log.d("Interceptor", "🔒 Sending Cookie header: $cookieHeader")
        } else {
            Log.d("Interceptor", "❌ No cookies available to send")
        }

        return chain.proceed(requestBuilder.build())
    }
}
