package com.project.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponses(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)

data class LoginResponses(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginResult")
    val loginResult: LoginResult
)

data class LoginResult(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String
)