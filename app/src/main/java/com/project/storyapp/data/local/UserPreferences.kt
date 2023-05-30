package com.project.storyapp.data.local

import android.content.Context
import android.provider.ContactsContract.Data
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){

    fun getUserToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_TOKEN_KEY]
        }
    }

    suspend fun saveUserToken(userToken: String) {
        dataStore.edit {it[USER_TOKEN_KEY] = userToken}
    }

    suspend fun removeUserToken() {
        dataStore.edit {it.remove(USER_TOKEN_KEY)}
    }


    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }
}