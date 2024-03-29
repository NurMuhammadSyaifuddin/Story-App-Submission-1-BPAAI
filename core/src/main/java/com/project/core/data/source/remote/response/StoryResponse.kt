package com.project.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("lat")
    val lat: Float? = null,

    @field:SerializedName("lon")
    val lon: Float? = null
)
