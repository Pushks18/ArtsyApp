// app/src/main/java/com/example/artsyapp/model/AuthResponse.kt
package com.example.artsyapp.model

data class AuthResponse(
    val token: String,
    val user: User
)