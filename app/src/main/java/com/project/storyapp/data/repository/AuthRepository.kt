package com.project.storyapp.data.repository

import com.project.storyapp.data.local.UserPreferences
import com.project.storyapp.data.remote.api.ApiService
import com.project.storyapp.data.remote.response.LoginResponses
import com.project.storyapp.data.remote.response.RegisterResponses
import com.project.storyapp.utils.handleError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject



class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponses>> {
        return flow {
            val responses = apiService.register(name, email, password)
            emit(Result.success(responses))
        }.catch {
            emit(Result.failure(Throwable(handleError(it))))
        }
    }


    suspend fun login(email: String, password: String): Flow<Result<LoginResponses>> {
        return flow {
            val responses = apiService.login(email, password)
            userPreferences.saveUserToken(
                responses.loginResult.token
            )
            emit(Result.success(responses))
        }.catch {
            emit(Result.failure(Throwable(handleError(it))))
        }
    }

    suspend fun logout(): Flow<Result<Unit>> {
        return flow {
            userPreferences.removeUserToken()
            emit(Result.success(Unit))
        }.catch {
            emit(Result.failure(Throwable(handleError(it))))
        }
    }

    fun getToken(): Flow<String?> {
        return userPreferences.getUserToken()
    }

    fun isLoggedIn(): Flow<Boolean> {
        return userPreferences.getUserToken().map { token ->
            token != null
        }
    }



}