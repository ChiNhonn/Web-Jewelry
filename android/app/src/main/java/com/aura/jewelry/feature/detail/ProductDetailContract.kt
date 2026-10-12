package com.aura.jewelry.feature.detail

import com.aura.jewelry.core.network.model.ProductDetailDto
import com.aura.jewelry.core.network.model.ProductVariantDto

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val product: ProductDetailDto? = null,
    val selectedVariant: ProductVariantDto? = null,
    val selectedImageIndex: Int = 0,
    val quantity: Int = 1,
    val isWishlisted: Boolean = false,
    val isAddingToCart: Boolean = false,
    val addedToCartSuccess: Boolean = false
) {
    val displayPrice: Double
        get() = selectedVariant?.salePrice ?: selectedVariant?.originalPrice
        ?: product?.variants?.firstOrNull()?.salePrice
        ?: product?.variants?.firstOrNull()?.originalPrice
        ?: 0.0

    val originalPrice: Double?
        get() = selectedVariant?.originalPrice?.takeIf {
            selectedVariant.salePrice != null && selectedVariant.salePrice < selectedVariant.originalPrice
        }

    val isOutOfStock: Boolean
        get() = (selectedVariant?.availableQuantity ?: 0) <= 0
}

sealed interface ProductDetailUiIntent {
    data class LoadProduct(val slug: String) : ProductDetailUiIntent
    data class SelectImage(val index: Int) : ProductDetailUiIntent
    data class SelectVariant(val variant: ProductVariantDto) : ProductDetailUiIntent
    data class UpdateQuantity(val newQuantity: Int) : ProductDetailUiIntent
    data object ToggleWishlist : ProductDetailUiIntent
    data class AddToCart(val navigateToCheckout: Boolean = false) : ProductDetailUiIntent
    data object ClearError : ProductDetailUiIntent
}

sealed interface ProductDetailUiEffect {
    data object NavigateBack : ProductDetailUiEffect
    data object NavigateToCart : ProductDetailUiEffect
    data object NavigateToCheckout : ProductDetailUiEffect
    data class ShowToast(val message: String) : ProductDetailUiEffect
}
