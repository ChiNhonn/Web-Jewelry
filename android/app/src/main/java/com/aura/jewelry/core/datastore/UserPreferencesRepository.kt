package com.aura.jewelry.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aura.jewelry.core.network.model.UserSummaryDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aura_user_prefs")

data class UserSession(
    val isLoggedIn: Boolean = false,
    val userId: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val roles: Set<String> = emptySet(),
    val accessToken: String? = null,
    val refreshToken: String? = null
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = longPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_FULL_NAME = stringPreferencesKey("user_full_name")
        val USER_ROLES = stringSetPreferencesKey("user_roles")
    }

    val userSession: Flow<UserSession> = context.dataStore.data.map { preferences ->
        val token = preferences[PreferencesKeys.ACCESS_TOKEN]
        val refreshToken = preferences[PreferencesKeys.REFRESH_TOKEN]
        val userId = preferences[PreferencesKeys.USER_ID]
        val username = preferences[PreferencesKeys.USERNAME]
        val email = preferences[PreferencesKeys.USER_EMAIL]
        val fullName = preferences[PreferencesKeys.USER_FULL_NAME]
        val roles = preferences[PreferencesKeys.USER_ROLES] ?: emptySet()

        UserSession(
            isLoggedIn = !token.isNullOrBlank(),
            userId = userId,
            username = username,
            email = email,
            fullName = fullName,
            roles = roles,
            accessToken = token,
            refreshToken = refreshToken
        )
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACCESS_TOKEN]
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.first()[PreferencesKeys.REFRESH_TOKEN]
    }

    suspend fun saveAuthSession(
        accessToken: String,
        refreshToken: String,
        user: UserSummaryDto
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = refreshToken
            preferences[PreferencesKeys.USER_ID] = user.id
            preferences[PreferencesKeys.USERNAME] = user.username
            preferences[PreferencesKeys.USER_EMAIL] = user.email
            preferences[PreferencesKeys.USER_FULL_NAME] = user.fullName ?: "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim()
            preferences[PreferencesKeys.USER_ROLES] = user.roles.toSet()
        }
    }

    suspend fun updateAccessToken(newAccessToken: String, newRefreshToken: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = newAccessToken
            if (newRefreshToken != null) {
                preferences[PreferencesKeys.REFRESH_TOKEN] = newRefreshToken
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
