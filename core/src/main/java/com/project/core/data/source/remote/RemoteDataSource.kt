package com.project.core.data.source.remote

import com.project.core.data.source.remote.network.ApiResponse
import com.project.core.data.source.remote.network.ApiService
import com.project.core.data.source.remote.response.*
import com.project.core.utils.reduceFileImage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

class RemoteDataSource(private val apiService: ApiService) {

    fun doLogin(email: String, password: String): Flow<ApiResponse<LoginResponse?>> =
        callbackFlow {
            val service = apiService.doLogin(email, password)
            try {
                service.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body() != null)
                                trySendBlocking(ApiResponse.Success(response.body())) else trySendBlocking(ApiResponse.Empty)
                        } else {
                            response.errorBody()?.let {
                                val errorResponse = JSONObject(it.string())
                                val errorMesage = errorResponse.getString("message")
                                trySendBlocking(ApiResponse.Error(errorMesage))
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        trySendBlocking(ApiResponse.Error(t.message.toString()))
                    }

                })

            } catch (e: Exception) {
                Timber.e(e.message.toString())
                trySendBlocking(ApiResponse.Error(e.message.toString()))
            }

            awaitClose { service.cancel() }
        }

    fun doRegister(
        name: String,
        email: String,
        password: String
    ): Flow<ApiResponse<RegisterResponse?>> =
        callbackFlow {

            val service = apiService.doRegister(name, email, password)

            try {
                service
                    .enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body() != null) trySendBlocking(
                                    ApiResponse.Success(
                                        response.body()
                                    )
                                ) else trySendBlocking(
                                    ApiResponse.Empty
                                )
                            } else {
                                response.errorBody()?.let {
                                    val errorResponse = JSONObject(it.string())
                                    val errorMessage = errorResponse.getString("message")
                                    trySendBlocking(ApiResponse.Error(errorMessage))
                                }
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            trySendBlocking(ApiResponse.Error(t.message.toString()))
                        }

                    })
            } catch (e: Exception) {
                Timber.e(e.message.toString())
                trySendBlocking(ApiResponse.Error(e.message.toString()))
            }

            awaitClose { service.cancel() }
        }

    fun doUploadStory(token: String, image: File, description: String) =
        callbackFlow {

            try {

                val file = reduceFileImage(image)

                val storyDescription = description.toRequestBody("text/plain".toMediaType())

                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultiPart =
                    MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

                val service = apiService.doUploadStory(token, imageMultiPart, storyDescription)

                service
                    .enqueue(object : Callback<StoryUploadResponse> {
                        override fun onResponse(
                            call: Call<StoryUploadResponse>,
                            response: Response<StoryUploadResponse>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body() != null) trySendBlocking(
                                    ApiResponse.Success(
                                        response.body()
                                    )
                                ) else trySendBlocking(
                                    ApiResponse.Empty
                                )
                            } else {
                                response.errorBody()?.let {
                                    val errorResponse = JSONObject(it.string())
                                    val errorMessage = errorResponse.getString("message")
                                    trySendBlocking(ApiResponse.Error(errorMessage))
                                }
                            }
                        }

                        override fun onFailure(call: Call<StoryUploadResponse>, t: Throwable) {
                            trySendBlocking(ApiResponse.Error(t.message.toString()))
                        }

                    })

                awaitClose { service.cancel() }
            } catch (e: Exception) {
                Timber.e(e.message.toString())
                trySendBlocking(ApiResponse.Error(e.message.toString()))
            }

        }

    suspend fun getStories(token: String): Flow<ApiResponse<List<StoryResponse>>> =
        callbackFlow {
            try {
                val response = apiService.getStories(token).listStory
                if (response.isNotEmpty()) trySendBlocking(ApiResponse.Success(response)) else trySendBlocking(
                    ApiResponse.Empty
                )
            } catch (e: Exception) {
                Timber.e(e.message.toString())
                trySendBlocking(ApiResponse.Error(e.message.toString()))
            }
        }

}