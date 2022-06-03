package com.project.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class StoriesResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<StoryResponse>
)
