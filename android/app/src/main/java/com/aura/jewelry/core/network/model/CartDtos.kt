package com.aura.jewelry.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequestDto(
    val variantId: Long,
    val quantity: Int = 1
)

@Serializable
data class UpdateCartItemRequestDto(
    val quantity: Int
)

@Serializable
data class CartItemDto(
    val id: Long,
    val variantId: Long,
    val productId: Long,
    val productName: String,
    val productSlug: String,
    val sku: String,
    val sizeOption: String? = null,
    val materialType: String? = null,
    val unitPrice: Double,
    val originalPrice: Double,
    val quantity: Int,
    val subtotal: Double,
    val thumbnailUrl: String? = null,
    val availableStock: Int = 0,
    val inStock: Boolean = true
)

@Serializable
data class CartResponseDto(
    val id: Long = 0,
    val items: List<CartItemDto> = emptyList(),
    val totalItems: Int = 0,
    val totalQuantity: Int = 0,
    val subtotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val estimatedTotal: Double = 0.0
)
