package com.example.artsyapp.repository

import android.util.Log
import com.example.artsyapp.model.*
import com.example.artsyapp.network.RetrofitInstance
import com.example.artsyapp.network.TokenManager
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import com.example.artsyapp.model.ErrorResponse

data class ErrorResponse(
    @SerializedName("error") val message: String
)

class UserRepository {

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        avatarUrl: String?
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        val payload = mutableMapOf(
            "fullName" to fullName.trim(),
            "email" to email.trim(),
            "password" to password.trim()
        )

        if (!avatarUrl.isNullOrBlank()) {
            payload["profileImageURL"] = avatarUrl.trim()
        }

        val resp = RetrofitInstance.api.signup(payload)

        if (resp.isSuccessful) {
            storeTokenFromHeader(resp)
            Result.success(resp.body()!!)
        } else {
            val errorMessage = extractErrorMessage(resp)
            Result.failure(Exception(errorMessage))
        }
    }


    suspend fun signIn(email: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        val resp = RetrofitInstance.api.signin(mapOf("email" to email, "password" to password))
        if (resp.isSuccessful) {
            storeTokenFromHeader(resp)
            Result.success(resp.body()!!)
        } else {
            val errorMessage = extractErrorMessage(resp)
            Result.failure(Exception(errorMessage))
        }
    }

    private fun storeTokenFromHeader(resp: Response<*>) {
        resp.headers().values("Set-Cookie").forEach { cookie ->
            if (cookie.startsWith("artsyToken=")) {
                val token = cookie.substringAfter("artsyToken=").substringBefore(';')
                TokenManager.artsyToken = token
                Log.d("UserRepo", "💾 Stored artsyToken=$token")
            }
            if (cookie.startsWith("jwtToken=")) {
                val jwtToken = cookie.substringAfter("jwtToken=").substringBefore(';')
                TokenManager.jwtToken = jwtToken
                Log.d("UserRepo", "💾 Stored jwtToken=$jwtToken")
            }
        }
    }



    private fun extractErrorMessage(resp: Response<*>): String {
        val errorJson = resp.errorBody()?.string()
        return try {
            val parsed = Gson().fromJson(errorJson, ErrorResponse::class.java)
            when (val msg = parsed.message) {
                is String -> msg
                is List<*> -> msg.joinToString(", ")
                else -> "Unknown error occurred"
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error parsing error response", e)
            "An error occurred"
        }
    }



    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        val resp = RetrofitInstance.api.getCurrentUser()
        if (!resp.isSuccessful) {
            Log.e("UserRepo", "getCurrentUser failed: ${resp.code()}")
            return@withContext null
        }
        resp.body()?.user
    }

    suspend fun signOut(): MessageResponse? = withContext(Dispatchers.IO) {
        val resp = RetrofitInstance.api.signout()
        if (!resp.isSuccessful) {
            Log.e("UserRepo", "signOut failed: ${resp.code()}")
            return@withContext null
        }
        TokenManager.clearAll()
        resp.body()
    }

    suspend fun deleteUser(): MessageResponse? = withContext(Dispatchers.IO) {
        val resp = RetrofitInstance.api.deleteUser()
        if (!resp.isSuccessful) {
            Log.e("UserRepo", "deleteUser failed: ${resp.code()}")
            return@withContext null
        }
        resp.body()
    }
}
