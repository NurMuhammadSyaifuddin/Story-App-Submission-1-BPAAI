package com.project.storyapp.di

import com.project.core.domain.usecase.StoryInteractor
import com.project.core.domain.usecase.StoryUseCase
import com.project.storyapp.ui.add.AddStoryViewModel
import com.project.storyapp.ui.login.LoginViewModel
import com.project.storyapp.ui.main.MainViewModel
import com.project.storyapp.ui.signup.SignUpViewModel
import com.project.storyapp.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<StoryUseCase> { StoryInteractor(get()) }
}

val viewModelModule = module {
    viewModel { SignUpViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { AddStoryViewModel(get(), get()) }
}
