package com.aura.jewelry.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val usernameOrEmail: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null
)

@Serializable
data class RefreshTokenRequestDto(
    val refreshToken: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 86400,
    val user: UserSummaryDto
)

@Serializable
data class UserSummaryDto(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val roles: List<String> = emptyList()
)
