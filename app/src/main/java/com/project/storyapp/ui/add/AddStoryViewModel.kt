package com.project.storyapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.core.domain.usecase.StoryUseCase
import com.project.core.preference.UserPreference
import kotlinx.coroutines.flow.first
import java.io.File

class AddStoryViewModel(private val pref: UserPreference, private val storyUseCase: StoryUseCase): ViewModel() {
    fun doUploadStory(token: String, image: File, description: String) = storyUseCase.doUploadStory(token, image, description).asLiveData()

    suspend fun getUserToken() = pref.getUserToken().first()
}