package com.aura.jewelry.data.repository

import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.common.safeApiCall
import com.aura.jewelry.core.datastore.UserPreferencesRepository
import com.aura.jewelry.core.datastore.UserSession
import com.aura.jewelry.core.network.api.AuthApiService
import com.aura.jewelry.core.network.model.AuthResponseDto
import com.aura.jewelry.core.network.model.LoginRequestDto
import com.aura.jewelry.core.network.model.RefreshTokenRequestDto
import com.aura.jewelry.core.network.model.RegisterRequestDto
import com.aura.jewelry.core.network.model.UserSummaryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val userSession: Flow<UserSession>
    fun login(usernameOrEmail: String, password: String): Flow<NetworkResult<AuthResponseDto>>
    fun register(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null
    ): Flow<NetworkResult<AuthResponseDto>>
    fun logout(): Flow<NetworkResult<Unit>>
    fun getCurrentUser(): Flow<NetworkResult<UserSummaryDto>>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userPreferencesRepository: UserPreferencesRepository
) : AuthRepository {

    override val userSession: Flow<UserSession> = userPreferencesRepository.userSession

    override fun login(usernameOrEmail: String, password: String): Flow<NetworkResult<AuthResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            authApiService.login(LoginRequestDto(usernameOrEmail = usernameOrEmail, password = password))
        }

        if (result is NetworkResult.Success) {
            val authData = result.data
            userPreferencesRepository.saveAuthSession(
                accessToken = authData.accessToken,
                refreshToken = authData.refreshToken,
                user = authData.user
            )
        }
        emit(result)
    }

    override fun register(
        username: String,
        email: String,
        password: String,
        firstName: String?,
        lastName: String?,
        phone: String?
    ): Flow<NetworkResult<AuthResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            authApiService.register(
                RegisterRequestDto(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone
                )
            )
        }

        if (result is NetworkResult.Success) {
            val authData = result.data
            userPreferencesRepository.saveAuthSession(
                accessToken = authData.accessToken,
                refreshToken = authData.refreshToken,
                user = authData.user
            )
        }
        emit(result)
    }

    override fun logout(): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        val currentRefreshToken = userPreferencesRepository.getRefreshToken()
        safeApiCall {
            authApiService.logout(
                if (currentRefreshToken != null) RefreshTokenRequestDto(currentRefreshToken) else null
            )
        }
        // Always purge local tokens upon logout
        userPreferencesRepository.clearSession()
        emit(NetworkResult.Success(Unit, "Đăng xuất thành công"))
    }

    override fun getCurrentUser(): Flow<NetworkResult<UserSummaryDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            authApiService.getCurrentUser()
        }
        emit(result)
    }
}
