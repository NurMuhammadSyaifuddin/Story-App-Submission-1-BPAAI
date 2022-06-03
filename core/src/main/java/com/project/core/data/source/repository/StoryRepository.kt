package com.project.core.data.source.repository

import com.project.core.data.source.Resource
import com.project.core.data.source.remote.RemoteDataSource
import com.project.core.data.source.remote.network.ApiResponse
import com.project.core.data.source.remote.response.LoginResponse
import com.project.core.data.source.remote.response.RegisterResponse
import com.project.core.data.source.remote.response.StoryUploadResponse
import com.project.core.domain.model.Login
import com.project.core.domain.model.Register
import com.project.core.domain.model.Story
import com.project.core.domain.model.StoryUpload
import com.project.core.domain.repository.IStoryRepository
import com.project.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File

class StoryRepository(private val remoteDataSource: RemoteDataSource): IStoryRepository {
    override fun doRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<Register>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.doRegister(name, email, password).first()) {
                is ApiResponse.Success -> emit(Resource.Success(DataMapper.mapResponseToDomainRegister(apiResponse.data as RegisterResponse)))
                is ApiResponse.Empty -> emit(Resource.Success(null))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }


    override fun doLogin(email: String, password: String): Flow<Resource<Login>> =
        flow {
            emit(Resource.Loading())
            when(val apiResponse = remoteDataSource.doLogin(email, password).first()){
                is ApiResponse.Success -> emit(Resource.Success(DataMapper.mapResponseToDomainLogin(apiResponse.data as LoginResponse)))
                is ApiResponse.Empty -> emit(Resource.Success(null))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun doUploadStory(token: String, image: File, description: String): Flow<Resource<StoryUpload>> =
        flow {
            emit(Resource.Loading())
            when(val apiResponse = remoteDataSource.doUploadStory(token, image, description).first()){
                is ApiResponse.Success -> emit(Resource.Success(DataMapper.mapResponseToDomainStoryUpload(apiResponse.data as StoryUploadResponse)))
                is ApiResponse.Empty -> emit(Resource.Success(null))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getStories(token: String): Flow<Resource<List<Story>>> =
        flow {
            emit(Resource.Loading())
            when(val apiResponse = remoteDataSource.getStories(token).first()){
                is ApiResponse.Success -> emit(Resource.Success(DataMapper.mapResponseToDomainStories(apiResponse.data)))
                is ApiResponse.Empty -> emit(Resource.Success(null))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }
}