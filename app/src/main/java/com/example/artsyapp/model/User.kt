// app/src/main/java/com/example/artsyapp/model/User.kt
package com.example.artsyapp.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,           // Mongo _id

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("profileImageURL")
    val profileImageURL: String?,

    @SerializedName("favorites")
    val favorites: List<Favorite>
)
