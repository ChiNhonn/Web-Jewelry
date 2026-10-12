package com.aura.jewelry.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aura.jewelry.core.network.model.ProductSummaryDto

/**
 * Room Entity caching jewelry products locally for offline browsing and fast first paint.
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val slug: String,
    val baseSku: String? = null,
    val categoryName: String? = null,
    val categorySlug: String? = null,
    val brand: String? = null,
    val materialOverview: String? = null,
    val isFeatured: Boolean = false,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val thumbnailUrl: String? = null,
    val status: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)

fun ProductSummaryDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        slug = slug,
        baseSku = baseSku,
        categoryName = categoryName,
        categorySlug = categorySlug,
        brand = brand,
        materialOverview = materialOverview,
        isFeatured = isFeatured,
        minPrice = minPrice,
        maxPrice = maxPrice,
        thumbnailUrl = thumbnailUrl,
        status = status,
        cachedAt = System.currentTimeMillis()
    )
}

fun ProductEntity.toDto(): ProductSummaryDto {
    return ProductSummaryDto(
        id = id,
        name = name,
        slug = slug,
        baseSku = baseSku,
        categoryName = categoryName,
        categorySlug = categorySlug,
        brand = brand,
        materialOverview = materialOverview,
        isFeatured = isFeatured,
        minPrice = minPrice,
        maxPrice = maxPrice,
        thumbnailUrl = thumbnailUrl,
        status = status
    )
}
