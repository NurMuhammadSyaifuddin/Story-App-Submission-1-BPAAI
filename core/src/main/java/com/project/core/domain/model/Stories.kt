package com.project.core.domain.model

import com.project.core.data.source.remote.response.StoryResponse

data class Stories(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)
