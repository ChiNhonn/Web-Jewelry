package com.aura.jewelry.core.network.model

import kotlinx.serialization.Serializable

/**
 * Standard unified response wrapper matching Spring Boot's ApiResponse<T>.
 */
@Serializable
data class ApiResponseDto<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null,
    val timestamp: String? = null
)

/**
 * Paginated response wrapper matching Spring Boot's PageResponse<T>.
 */
@Serializable
data class PageResponseDto<T>(
    val content: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 10,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true
)
