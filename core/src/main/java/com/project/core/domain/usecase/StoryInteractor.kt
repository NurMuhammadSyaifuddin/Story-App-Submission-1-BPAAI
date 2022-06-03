package com.project.core.domain.usecase

import com.project.core.data.source.Resource
import com.project.core.domain.model.Login
import com.project.core.domain.model.Register
import com.project.core.domain.model.Story
import com.project.core.domain.model.StoryUpload
import com.project.core.domain.repository.IStoryRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class StoryInteractor(private val storyRepository: IStoryRepository): StoryUseCase {
    override fun doRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<Register>> = storyRepository.doRegister(name, email, password)

    override fun doLogin(email: String, password: String): Flow<Resource<Login>> = storyRepository.doLogin(email, password)

    override fun doUploadStory(
        token: String,
        image: File,
        description: String
    ): Flow<Resource<StoryUpload>> = storyRepository.doUploadStory(token, image, description)
    override fun getStories(token: String): Flow<Resource<List<Story>>>  = storyRepository.getStories(token)
}