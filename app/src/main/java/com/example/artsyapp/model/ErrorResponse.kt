package com.example.artsyapp.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message")
    val message: Any // Can be String or List<String>
)
