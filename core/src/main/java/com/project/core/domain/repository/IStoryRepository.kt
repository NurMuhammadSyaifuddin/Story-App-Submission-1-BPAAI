package com.project.core.domain.repository

import com.project.core.data.source.Resource
import com.project.core.domain.model.Login
import com.project.core.domain.model.Register
import com.project.core.domain.model.Story
import com.project.core.domain.model.StoryUpload
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IStoryRepository {
    fun doRegister(name: String, email: String, password: String): Flow<Resource<Register>>
    fun doLogin(email: String, password: String): Flow<Resource<Login>>
    fun doUploadStory(token: String, image: File, description: String): Flow<Resource<StoryUpload>>
    fun getStories(token: String): Flow<Resource<List<Story>>>
}