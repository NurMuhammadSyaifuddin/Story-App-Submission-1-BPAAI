package com.project.storyapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.project.core.domain.usecase.StoryUseCase
import com.project.core.preference.UserPreference
import kotlinx.coroutines.flow.first

class MainViewModel(private val pref: UserPreference, private val storyUseCase: StoryUseCase): ViewModel() {
    fun getStories(token: String) = storyUseCase.getStories(token).cachedIn(viewModelScope).asLiveData()

    suspend fun getUserToken() = pref.getUserToken().first()

}