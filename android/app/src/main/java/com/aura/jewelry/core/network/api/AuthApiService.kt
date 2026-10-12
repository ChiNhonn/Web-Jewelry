package com.aura.jewelry.core.network.api

import com.aura.jewelry.core.network.model.ApiResponseDto
import com.aura.jewelry.core.network.model.AuthResponseDto
import com.aura.jewelry.core.network.model.LoginRequestDto
import com.aura.jewelry.core.network.model.RefreshTokenRequestDto
import com.aura.jewelry.core.network.model.RegisterRequestDto
import com.aura.jewelry.core.network.model.UserSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): ApiResponseDto<AuthResponseDto>

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): ApiResponseDto<AuthResponseDto>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequestDto
    ): ApiResponseDto<AuthResponseDto>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequestDto? = null
    ): ApiResponseDto<Unit?>

    @GET("api/v1/auth/me")
    suspend fun getCurrentUser(): ApiResponseDto<UserSummaryDto>
}
