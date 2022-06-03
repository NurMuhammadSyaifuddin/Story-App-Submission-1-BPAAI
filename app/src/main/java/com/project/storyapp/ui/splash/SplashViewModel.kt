package com.project.storyapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.core.preference.UserPreference

class SplashViewModel(private val pref: UserPreference): ViewModel() {
    fun getUserIsLogin() = pref.getUserIsLogin().asLiveData()
}