package com.project.storyapp

import android.app.Application
import com.project.core.di.databaseModule
import com.project.core.di.networkModule
import com.project.core.di.preferenceModule
import com.project.core.di.repositoryModule
import com.project.storyapp.di.useCaseModule
import com.project.storyapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@BaseApplication)
            modules(
                networkModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                preferenceModule,
                databaseModule
            )
        }

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}