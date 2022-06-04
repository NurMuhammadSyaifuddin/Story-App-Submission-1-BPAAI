package com.project.storyapp.ui.settings

import androidx.lifecycle.ViewModel
import com.project.core.preference.UserPreference

class SettingsViewModel(private val pref: UserPreference): ViewModel() {
    suspend fun removeUserIsLogin() = pref.removeUserIsLogin()
    suspend fun removeUserToken() = pref.removeUserToken()
}