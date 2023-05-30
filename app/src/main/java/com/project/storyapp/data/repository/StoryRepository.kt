package com.project.storyapp.data.repository

import com.project.storyapp.data.remote.api.ApiService
import com.project.storyapp.data.remote.response.AddStoryResponse
import com.project.storyapp.data.remote.response.StoriesResponse
import com.project.storyapp.data.remote.response.StoryDetailResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class StoryRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun addStory(
        description: String,
        photo: MultipartBody.Part,
        lat: Double?,
        lon: Double?,
        authHeader: String
        ): Flow<Result<AddStoryResponse>> {
        return flow {
            val response = apiService.addStory(description, photo, lat, lon, authHeader)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(it))
        }
    }

    fun getListStories(
        page: Int?,
        size: Int?,
        location: Int = 0,
        authHeader: String
    ): Flow<Result<StoriesResponse>> {
        return flow {
            val response = apiService.getListStories(page, size, location, authHeader)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(it))
        }
    }

    fun getStoryDetail(
        id: String,
        authHeader: String
    ): Flow<Result<StoryDetailResponse>> {
        return flow {
            val response = apiService.getStoryDetail(id, authHeader)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(it))
        }
    }


}