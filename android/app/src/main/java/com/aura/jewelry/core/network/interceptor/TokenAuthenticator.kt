package com.aura.jewelry.core.network.interceptor

import com.aura.jewelry.BuildConfig
import com.aura.jewelry.core.datastore.UserPreferencesRepository
import com.aura.jewelry.core.network.model.ApiResponseDto
import com.aura.jewelry.core.network.model.AuthResponseDto
import com.aura.jewelry.core.network.model.RefreshTokenRequestDto
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * OkHttp Authenticator that automatically catches HTTP 401 Unauthorized responses,
 * calls Spring Boot's /api/v1/auth/refresh endpoint to exchange refreshToken for a new token,
 * updates DataStore, and transparently retries the failed request.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val json: Json
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loop if already retried
        if (responseCount(response) >= 2) {
            return null
        }

        // Do not attempt refresh on auth endpoints
        val requestUrl = response.request.url.toString()
        if (requestUrl.contains("/api/v1/auth/login") || requestUrl.contains("/api/v1/auth/refresh")) {
            return null
        }

        synchronized(this) {
            val currentRefreshToken = runBlocking { userPreferencesRepository.getRefreshToken() }
            if (currentRefreshToken.isNullOrBlank()) {
                runBlocking { userPreferencesRepository.clearSession() }
                return null
            }

            // Execute synchronous refresh request
            val newTokens = refreshAccessToken(currentRefreshToken)
            return if (newTokens != null) {
                runBlocking {
                    userPreferencesRepository.updateAccessToken(
                        newAccessToken = newTokens.accessToken,
                        newRefreshToken = newTokens.refreshToken
                    )
                }
                // Retry original request with newly acquired access token
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                runBlocking { userPreferencesRepository.clearSession() }
                null
            }
        }
    }

    private fun refreshAccessToken(refreshToken: String): AuthResponseDto? {
        val client = OkHttpClient.Builder().build()
        val payload = json.encodeToString(RefreshTokenRequestDto(refreshToken))
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}api/v1/auth/refresh")
            .post(payload.toRequestBody(mediaType))
            .build()

        return try {
            client.newCall(request).execute().use { res ->
                if (res.isSuccessful) {
                    val bodyString = res.body?.string() ?: return null
                    val apiResponse = json.decodeFromString<ApiResponseDto<AuthResponseDto>>(bodyString)
                    apiResponse.data
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
