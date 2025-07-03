package com.example.artsyapp.repository

import android.util.Log
import com.example.artsyapp.model.AuthResponse
import com.example.artsyapp.network.RetrofitInstance
import com.example.artsyapp.network.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

object AuthRepository {
    suspend fun signup(body: Map<String, String>): AuthResponse? =
        withContext(Dispatchers.IO) {
            val resp: Response<AuthResponse> = RetrofitInstance.api.signup(body)
            // grab artsyToken cookie
            resp.headers()
                .values("Set-Cookie")
                .firstOrNull { it.startsWith("artsyToken=") }
                ?.let { cookie ->
                    val artsyToken = cookie.substringAfter("artsyToken=").substringBefore(';')
                    TokenManager.artsyToken = artsyToken   // ✅ correct field name
                    Log.d("AuthRepository", "💾 artsyToken=$artsyToken")
                }
            resp.headers()
                .values("Set-Cookie")
                .firstOrNull { it.startsWith("jwtToken=") }
                ?.let { cookie ->
                    val jwtToken = cookie.substringAfter("jwtToken=").substringBefore(';')
                    TokenManager.jwtToken = jwtToken
                    Log.d("AuthRepository", "💾 jwtToken=$jwtToken")
                }
            resp.body()
        }

    suspend fun signin(body: Map<String, String>): AuthResponse? =
        withContext(Dispatchers.IO) {
            val resp: Response<AuthResponse> = RetrofitInstance.api.signin(body)
            resp.headers()
                .values("Set-Cookie")
                .firstOrNull { it.startsWith("artsyToken=") }
                ?.let { cookie ->
                    val artsyToken = cookie.substringAfter("artsyToken=").substringBefore(';')
                    TokenManager.artsyToken = artsyToken   // ✅ correct field name
                    Log.d("AuthRepository", "💾 artsyToken=$artsyToken")
                }
            resp.headers()
                .values("Set-Cookie")
                .firstOrNull { it.startsWith("jwtToken=") }
                ?.let { cookie ->
                    val jwtToken = cookie.substringAfter("jwtToken=").substringBefore(';')
                    TokenManager.jwtToken = jwtToken
                    Log.d("AuthRepository", "💾 jwtToken=$jwtToken")
                }
            resp.body()
        }
}
