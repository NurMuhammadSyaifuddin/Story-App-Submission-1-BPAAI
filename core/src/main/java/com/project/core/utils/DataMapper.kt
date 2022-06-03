package com.project.core.utils

import com.project.core.data.source.remote.response.LoginResponse
import com.project.core.data.source.remote.response.RegisterResponse
import com.project.core.data.source.remote.response.StoryResponse
import com.project.core.data.source.remote.response.StoryUploadResponse
import com.project.core.domain.model.Login
import com.project.core.domain.model.Register
import com.project.core.domain.model.Story
import com.project.core.domain.model.StoryUpload

object DataMapper {

    fun mapResponseToDomainRegister(input: RegisterResponse): Register =
        Register(
            input.error,
            input.message
        )

    fun mapResponseToDomainLogin(input: LoginResponse): Login =
        Login(
            input.error,
            input.message,
            input.loginResult
        )

    fun mapResponseToDomainStoryUpload(input: StoryUploadResponse): StoryUpload =
        StoryUpload(
            input.error,
            input.message
        )

    fun mapResponseToDomainStories(input: List<StoryResponse>): List<Story> =
        input.map {
            Story(
                it.id,
                it.name,
                it.description,
                it.photoUrl,
                it.createdAt,
                it.lat,
                it.lon
            )
        }

}