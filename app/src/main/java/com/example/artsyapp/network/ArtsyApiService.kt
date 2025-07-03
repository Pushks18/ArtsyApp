// app/src/main/java/com/example/artsyapp/network/ArtsyApiService.kt
package com.example.artsyapp.network

import com.example.artsyapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ArtsyApiService {
    @POST("user/signup")
    suspend fun signup(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("user/signin")
    suspend fun signin(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("user/me")
    suspend fun getCurrentUser(): Response<UserWrapper>

    @POST("user/signout")
    suspend fun signout(): Response<MessageResponse>

    @DELETE("user/delete")
    suspend fun deleteUser(): Response<MessageResponse>

    @GET("artsy/search")
    suspend fun searchArtists(@Query("query") query: String): Response<ArtsySearchResponse>

    @GET("artsy/favorites")
    suspend fun getFavoriteArtists(): Response<FavoritesResponse>

    @GET("artsy/artists/{id}")
    suspend fun getArtistById(@Path("id") id: String): Response<Artist>

    @GET("artsy/artworks/{id}")
    suspend fun getArtworks(
        @Path("id") artistId: String
    ): Response<ArtworksResponse>



    @GET("artsy/genes")
    suspend fun getGenes(@QueryMap params: Map<String, String>): GenesResponse

    @POST("artsy/artist/{artistId}")
    suspend fun favoriteArtist(
        @Path("artistId") artistId: String,
        @Body body: FavoriteRequest
    ): Response<MessageResponse>

    @DELETE("artsy/delete/{artistId}")
    suspend fun unfavoriteArtist(
        @Path("artistId") artistId: String
    ): Response<MessageResponse>

    @GET("artsy/artists/similar/{artistId}")
    suspend fun getSimilarArtists(
        @Path("artistId") artistId: String
    ): Response<ArtsySearchResponse>


    @HTTP(method = "DELETE", path = "artsy/favorite", hasBody = true)
    suspend fun unfavoriteArtist(@Body body: Map<String, String>): Response<Unit>

}
