package com.aura.jewelry.core.network.api

import com.aura.jewelry.core.network.model.ApiResponseDto
import com.aura.jewelry.core.network.model.CategoryDto
import com.aura.jewelry.core.network.model.PageResponseDto
import com.aura.jewelry.core.network.model.ProductDetailDto
import com.aura.jewelry.core.network.model.ProductSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {

    @GET("api/v1/products")
    suspend fun filterProducts(
        @Query("keyword") keyword: String? = null,
        @Query("categorySlug") categorySlug: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("isFeatured") isFeatured: Boolean? = null,
        @Query("sortBy") sortBy: String? = "createdAt",
        @Query("direction") direction: String? = "DESC",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponseDto<PageResponseDto<ProductSummaryDto>>

    @GET("api/v1/products/{slug}")
    suspend fun getProductDetail(
        @Path("slug") slug: String
    ): ApiResponseDto<ProductDetailDto>

    @GET("api/v1/categories")
    suspend fun getCategories(): ApiResponseDto<List<CategoryDto>>

    @GET("api/v1/categories/{slug}")
    suspend fun getCategoryBySlug(
        @Path("slug") slug: String
    ): ApiResponseDto<CategoryDto>
}
