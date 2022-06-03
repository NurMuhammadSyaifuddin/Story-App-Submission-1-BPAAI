package com.project.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.project.core.BuildConfig
import com.project.core.data.source.remote.RemoteDataSource
import com.project.core.data.source.remote.network.ApiService
import com.project.core.data.source.repository.StoryRepository
import com.project.core.domain.repository.IStoryRepository
import com.project.core.preference.UserPreference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preference")

val preferenceModule = module {
    factory { get<Context>().dataStore }
    single { UserPreference(get()) }
}

val networkModule = module {
    single {
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG){
            val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logging)
        }


        httpClient
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { RemoteDataSource(get()) }
    single<IStoryRepository> {
        StoryRepository(get())
    }
}

