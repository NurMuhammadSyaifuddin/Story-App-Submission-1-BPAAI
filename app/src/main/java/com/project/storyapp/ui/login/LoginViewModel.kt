package com.project.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.core.domain.usecase.StoryUseCase
import com.project.core.preference.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference, private val storyUseCase: StoryUseCase): ViewModel() {
    fun doLogin(email: String, password: String) = storyUseCase.doLogin(email, password).asLiveData()

    fun saveUserSession(isLogin: Boolean){
        viewModelScope.launch {
            pref.saveUserIsLogin(isLogin)
        }
    }

    fun saveUserToken(token: String){
        viewModelScope.launch {
            pref.saveUserToken(token)
        }
    }
}