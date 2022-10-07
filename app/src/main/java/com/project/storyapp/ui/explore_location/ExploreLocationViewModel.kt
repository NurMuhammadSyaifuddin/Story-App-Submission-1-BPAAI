package com.project.storyapp.ui.explore_location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.core.domain.usecase.StoryUseCase
import com.project.core.preference.UserPreference
import com.project.storyapp.utils.indonesianLocation
import kotlinx.coroutines.flow.first

class ExploreLocationViewModel(private val pref: UserPreference, private val storyUseCase: StoryUseCase): ViewModel() {

    val coordinateTemp = MutableLiveData(indonesianLocation)

    fun getStoriesWithLocation(token: String, size: Int) = storyUseCase.getStoriesLocation(token, size).asLiveData()

    suspend fun getUserToken() = pref.getUserToken().first()

}