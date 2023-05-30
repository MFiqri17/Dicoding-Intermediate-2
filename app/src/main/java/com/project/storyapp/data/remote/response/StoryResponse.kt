package com.project.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class Story(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("photoUrl")
    val photoUrl: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("lon")
    val lon: Double?,
    @SerializedName("lat")
    val lat: Double?
)

data class StoriesResponse(
    @SerializedName("listStory")
    val listStory: List<Story>,
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: Boolean,
)

data class AddStoryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: Boolean
)

data class StoryDetailResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("story")
    val storyDetail: Story
)
