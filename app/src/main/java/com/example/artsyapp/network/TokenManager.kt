package com.example.artsyapp.network

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "token_prefs"
    private const val KEY_ARTSY_TOKEN = "artsyToken"
    private const val KEY_JWT_TOKEN = "jwtToken"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var artsyToken: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_ARTSY_TOKEN, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_ARTSY_TOKEN, value).apply()
            }
        }

    var jwtToken: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_JWT_TOKEN, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_JWT_TOKEN, value).apply()
            }
        }

    fun clearAll() {
        if (::prefs.isInitialized) {
            prefs.edit().clear().apply()
        }
    }
}
