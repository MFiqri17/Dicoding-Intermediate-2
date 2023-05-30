package com.project.storyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.project.storyapp.data.remote.response.AddStoryResponse
import com.project.storyapp.data.repository.AuthRepository
import com.project.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) :
    ViewModel() {

    suspend fun addStory(
        description: String,
        photo: MultipartBody.Part,
        lat: Double?,
        lon: Double?,
        authHeader: String
    ): Flow<Result<AddStoryResponse>> {
        return storyRepository.addStory(description, photo, lat, lon, authHeader)
    }

    fun getToken(): Flow<String?> {
        return authRepository.getToken()
    }
}