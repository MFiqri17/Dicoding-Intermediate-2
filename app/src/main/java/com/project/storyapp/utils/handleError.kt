package com.project.storyapp.utils
import com.project.storyapp.data.remote.response.ErrorResponses

import com.google.gson.Gson
import retrofit2.HttpException

fun handleError(error: Throwable): String? {
    return when (error) {
        is HttpException -> {
            val response = error.response()
            if (response != null) {
                val errorMessage = response.errorBody()?.string()
                if (!errorMessage.isNullOrEmpty()) {
                    val errorResponse = Gson().fromJson(errorMessage, ErrorResponses::class.java)
                    errorResponse.message
                } else {
                    null
                }
            } else {
                null
            }
        }
        else -> error.message
    }
}