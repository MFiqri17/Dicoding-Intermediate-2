package com.project.storyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.project.storyapp.data.local.UserPreferences
import com.project.storyapp.data.remote.response.StoriesResponse
import com.project.storyapp.data.repository.AuthRepository
import com.project.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val storyRepository: StoryRepository, private val authRepository: AuthRepository) : ViewModel() {

    fun getListStories(
        page: Int?,
        size: Int?,
        location: Int = 0,
        authHeader: String
    ): Flow<Result<StoriesResponse>> {
        return storyRepository.getListStoriesWithoutPaging(page, size, location, authHeader)
    }

    fun getToken(): Flow<String?> {
        return authRepository.getToken()
    }
}