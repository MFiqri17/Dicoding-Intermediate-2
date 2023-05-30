package com.project.storyapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.project.storyapp.data.remote.response.LoginResponses
import com.project.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    suspend fun login(email: String, password: String): Flow<Result<LoginResponses>> {
        return authRepository.login(email, password)
    }
}