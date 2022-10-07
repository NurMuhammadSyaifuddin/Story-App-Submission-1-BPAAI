package com.project.storyapp.ui.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.core.domain.usecase.StoryUseCase
import com.project.core.preference.UserPreference
import kotlinx.coroutines.flow.first
import java.io.File

class AddStoryViewModel(private val pref: UserPreference, private val storyUseCase: StoryUseCase): ViewModel() {

    val isLocationPicked = MutableLiveData(false)
    val latitude = MutableLiveData(0.0)
    val longitude = MutableLiveData(0.0)

    fun doUploadStory(token: String, image: File, description: String, lat: String, lon: String) = storyUseCase.doUploadStory(token, image, description, lat, lon).asLiveData()

    suspend fun getUserToken() = pref.getUserToken().first()
}