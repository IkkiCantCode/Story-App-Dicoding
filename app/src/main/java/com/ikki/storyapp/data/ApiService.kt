package com.ikki.storyapp.data

import com.ikki.storyapp.response.LoginResponse
import com.ikki.storyapp.response.RegisterResponse
import com.ikki.storyapp.response.StoryResponse
import com.ikki.storyapp.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: Map<String, String>, @Header("Authorization") token: String? = null): LoginResponse

    @POST("register")
    suspend fun register(@Body registerRequest: Map<String, String>): RegisterResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): UploadResponse
}