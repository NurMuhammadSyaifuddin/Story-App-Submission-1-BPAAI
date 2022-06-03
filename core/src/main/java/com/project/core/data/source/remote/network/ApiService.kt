package com.project.core.data.source.remote.network

import com.project.core.data.source.remote.response.LoginResponse
import com.project.core.data.source.remote.response.RegisterResponse
import com.project.core.data.source.remote.response.StoriesResponse
import com.project.core.data.source.remote.response.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    fun doLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun doRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @Multipart
    @POST("stories")
    fun doUploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<StoryUploadResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String
    ): StoriesResponse
}