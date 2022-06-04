package com.project.core.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference(private val dataStore: DataStore<Preferences>) {

    private val userKey = booleanPreferencesKey(USER_KEY)
    private val userToken = stringPreferencesKey(USER_TOKEN)

    suspend fun saveUserIsLogin(isLogin: Boolean){
        dataStore.edit {
            it[userKey] = isLogin
        }
    }

    fun getUserIsLogin(): Flow<Boolean> =
        dataStore.data.map {
            it[userKey] ?: false
        }

    suspend fun removeUserIsLogin(){
        dataStore.edit {
            if(it.contains(userKey)){
                it.remove(userKey)
            }
        }
    }

    suspend fun saveUserToken(token: String) =
        dataStore.edit {
            it[userToken] = token
        }

    fun getUserToken(): Flow<String?> =
        dataStore.data.map {
            it[userToken]
        }

    suspend fun removeUserToken(){
        dataStore.edit {
            if (it.contains(userToken)){
                it.remove(userToken)
            }
        }
    }

    companion object{
        private const val USER_KEY = "user_key"
        private const val USER_TOKEN = "user_token"
    }
}