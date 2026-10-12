package com.aura.jewelry.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShippingAddressDto(
    @SerialName("receiverName")
    val receiverName: String = "",
    val phone: String = "",
    val streetAddress: String = "",
    val ward: String = "",
    val district: String = "",
    @SerialName("province")
    val province: String = "",
    val postalCode: String? = null,
    val country: String = "Vietnam",
    val isDefault: Boolean = false
) {
    val fullName: String get() = receiverName
    val city: String get() = province
}

@Serializable
data class CreateOrderRequestDto(
    val shippingAddressId: Long? = null,
    val shippingAddress: ShippingAddressDto? = null,
    val paymentMethod: String = "COD",
    val couponCode: String? = null,
    val customerNote: String? = null
)

@Serializable
data class CancelOrderRequestDto(
    val reason: String = "Customer requested cancellation"
)

@Serializable
data class OrderItemDto(
    val id: Long = 0,
    val variantId: Long,
    val productId: Long,
    val productName: String,
    val productSlug: String = "",
    val sku: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double,
    val thumbnailUrl: String? = null,
    val sizeOption: String? = null,
    val materialType: String? = null
)

@Serializable
data class OrderSummaryDto(
    val id: Long,
    val orderCode: String,
    val orderStatus: String,
    val paymentStatus: String,
    val paymentMethod: String,
    val totalAmount: Double,
    val totalQuantity: Int,
    val createdAt: String? = null
)

@Serializable
data class OrderResponseDto(
    val id: Long,
    val orderCode: String,
    val userId: Long = 0,
    val customerName: String? = null,
    val customerEmail: String? = null,
    val customerPhone: String? = null,
    val orderStatus: String,
    val paymentStatus: String,
    val paymentMethod: String,
    val transactionCode: String? = null,
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val shippingFee: Double = 0.0,
    val totalAmount: Double,
    val couponCode: String? = null,
    val shippingAddressSnapshot: String? = null,
    val customerNote: String? = null,
    val items: List<OrderItemDto> = emptyList(),
    val totalQuantity: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
