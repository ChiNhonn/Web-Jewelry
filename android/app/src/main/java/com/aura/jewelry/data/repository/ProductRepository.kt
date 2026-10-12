package com.aura.jewelry.data.repository

import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.common.safeApiCall
import com.aura.jewelry.core.database.dao.CategoryDao
import com.aura.jewelry.core.database.dao.ProductDao
import com.aura.jewelry.core.database.model.toDto
import com.aura.jewelry.core.database.model.toEntity
import com.aura.jewelry.core.network.api.ProductApiService
import com.aura.jewelry.core.network.model.CategoryDto
import com.aura.jewelry.core.network.model.PageResponseDto
import com.aura.jewelry.core.network.model.ProductDetailDto
import com.aura.jewelry.core.network.model.ProductSummaryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ProductRepository {
    fun filterProducts(
        keyword: String? = null,
        categorySlug: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        isFeatured: Boolean? = null,
        sortBy: String? = "createdAt",
        direction: String? = "DESC",
        page: Int = 0,
        size: Int = 10
    ): Flow<NetworkResult<PageResponseDto<ProductSummaryDto>>>

    fun getProductDetail(slug: String): Flow<NetworkResult<ProductDetailDto>>

    fun getCategories(): Flow<NetworkResult<List<CategoryDto>>>

    fun getCategoryBySlug(slug: String): Flow<NetworkResult<CategoryDto>>

    fun getCachedProducts(): Flow<List<ProductSummaryDto>>
}

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productApiService: ProductApiService,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) : ProductRepository {

    override fun filterProducts(
        keyword: String?,
        categorySlug: String?,
        minPrice: Double?,
        maxPrice: Double?,
        isFeatured: Boolean?,
        sortBy: String?,
        direction: String?,
        page: Int,
        size: Int
    ): Flow<NetworkResult<PageResponseDto<ProductSummaryDto>>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            productApiService.filterProducts(
                keyword = keyword,
                categorySlug = categorySlug,
                minPrice = minPrice,
                maxPrice = maxPrice,
                isFeatured = isFeatured,
                sortBy = sortBy,
                direction = direction,
                page = page,
                size = size
            )
        }

        if (result is NetworkResult.Success) {
            // Cache fresh products to Room database
            productDao.upsertProducts(result.data.content.map { it.toEntity() })
            emit(result)
        } else if (result is NetworkResult.Error) {
            // Offline fallback: load cached products from Room
            val cached = productDao.getAllProducts().first()
            if (cached.isNotEmpty()) {
                val cachedContent = cached.map { it.toDto() }
                emit(
                    NetworkResult.Success(
                        data = PageResponseDto(
                            content = cachedContent,
                            page = 0,
                            size = cachedContent.size,
                            totalElements = cachedContent.size.toLong(),
                            totalPages = 1,
                            first = true,
                            last = true
                        ),
                        message = "Hiển thị dữ liệu ngoại tuyến đã lưu"
                    )
                )
            } else {
                emit(result)
            }
        }
    }

    override fun getProductDetail(slug: String): Flow<NetworkResult<ProductDetailDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            productApiService.getProductDetail(slug)
        }
        emit(result)
    }

    override fun getCategories(): Flow<NetworkResult<List<CategoryDto>>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            productApiService.getCategories()
        }

        if (result is NetworkResult.Success) {
            categoryDao.upsertCategories(result.data.mapIndexed { index, cat -> cat.toEntity(index) })
            emit(result)
        } else if (result is NetworkResult.Error) {
            val cached = categoryDao.getAllCategories().first()
            if (cached.isNotEmpty()) {
                emit(NetworkResult.Success(cached.map { it.toDto() }, "Danh mục lưu ngoại tuyến"))
            } else {
                emit(result)
            }
        }
    }

    override fun getCategoryBySlug(slug: String): Flow<NetworkResult<CategoryDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            productApiService.getCategoryBySlug(slug)
        }
        emit(result)
    }

    override fun getCachedProducts(): Flow<List<ProductSummaryDto>> {
        return productDao.getAllProducts().map { list ->
            list.map { it.toDto() }
        }
    }
}
