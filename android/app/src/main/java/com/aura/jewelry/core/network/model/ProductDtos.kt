package com.aura.jewelry.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Long,
    val name: String,
    val slug: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val parentId: Long? = null,
    val children: List<CategoryDto> = emptyList()
)

@Serializable
data class ProductSummaryDto(
    val id: Long,
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
    val createdAt: String? = null
)

@Serializable
data class ProductImageDto(
    val id: Long = 0,
    val imageUrl: String,
    val altText: String? = null,
    val isPrimary: Boolean = false,
    val displayOrder: Int = 0
)

@Serializable
data class ProductVariantDto(
    val id: Long,
    val sku: String,
    val sizeOption: String? = null,
    val materialType: String? = null,
    val originalPrice: Double,
    val salePrice: Double? = null,
    val status: String? = null,
    val stockQuantity: Int = 0,
    val availableQuantity: Int = 0
)

@Serializable
data class ProductDetailDto(
    val id: Long,
    val name: String,
    val slug: String,
    val baseSku: String? = null,
    val category: CategoryDto? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val materialOverview: String? = null,
    val brand: String? = null,
    val isFeatured: Boolean = false,
    val status: String? = null,
    val images: List<ProductImageDto> = emptyList(),
    val variants: List<ProductVariantDto> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)
