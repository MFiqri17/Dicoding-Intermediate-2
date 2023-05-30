package com.project.storyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.project.storyapp.data.remote.response.StoryDetailResponse
import com.project.storyapp.data.repository.AuthRepository
import com.project.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) :
    ViewModel() {
    fun getStoryDetail(
        id: String,
        authHeader: String
    ): Flow<Result<StoryDetailResponse>> {
        return storyRepository.getStoryDetail(id, authHeader)
    }

    fun getToken(): Flow<String?> {
        return authRepository.getToken()
    }
}