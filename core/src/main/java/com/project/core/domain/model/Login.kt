package com.project.core.domain.model

import com.project.core.data.source.remote.response.UserResponse

data class Login(
    val error: Boolean,
    val message: String,
    val loginResult: UserResponse
)