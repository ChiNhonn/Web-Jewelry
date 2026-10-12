package com.aura.jewelry.core.network.interceptor

import com.aura.jewelry.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Interceptor that attaches the JWT Bearer Token to authenticated outgoing requests.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // If request already specifies Authorization header, proceed
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Retrieve current JWT token synchronously
        val token = runBlocking {
            userPreferencesRepository.getAccessToken()
        }

        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
