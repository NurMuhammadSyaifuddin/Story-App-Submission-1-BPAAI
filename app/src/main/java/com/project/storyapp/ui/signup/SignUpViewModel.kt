package com.project.storyapp.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.core.domain.usecase.StoryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignUpViewModel(private val storyUseCase: StoryUseCase) : ViewModel() {
    fun doRegister(name: String, email: String, password: String) =
            storyUseCase.doRegister(name, email, password).asLiveData()
}