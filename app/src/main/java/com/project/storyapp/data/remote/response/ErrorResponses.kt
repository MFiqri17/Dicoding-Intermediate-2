package com.project.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponses(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String
)