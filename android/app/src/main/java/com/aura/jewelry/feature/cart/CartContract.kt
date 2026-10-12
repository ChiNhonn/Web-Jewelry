package com.aura.jewelry.feature.cart

import com.aura.jewelry.core.network.model.CartResponseDto

data class CartUiState(
    val isLoading: Boolean = false,
    val isUpdatingItemId: Long? = null,
    val errorMessage: String? = null,
    val cart: CartResponseDto? = null,
    val couponCode: String = "",
    val couponDiscount: Double = 0.0,
    val couponApplied: Boolean = false,
    val couponMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val showClearCartDialog: Boolean = false
) {
    val totalAmount: Double
        get() {
            val subtotal = cart?.subtotal ?: 0.0
            val shipping = cart?.shippingFee ?: 0.0
            return maxOf(0.0, subtotal + shipping - couponDiscount)
        }

    val isCartEmpty: Boolean
        get() = cart == null || cart.items.isEmpty()
}

sealed interface CartUiIntent {
    data object LoadCart : CartUiIntent
    data class UpdateQuantity(val itemId: Long, val quantity: Int) : CartUiIntent
    data class RemoveItem(val itemId: Long) : CartUiIntent
    data object ClearCart : CartUiIntent
    data class UpdateCouponCode(val code: String) : CartUiIntent
    data object ApplyCoupon : CartUiIntent
    data class SetClearCartDialogVisible(val visible: Boolean) : CartUiIntent
    data object ProceedToCheckout : CartUiIntent
    data object ClearError : CartUiIntent
}

sealed interface CartUiEffect {
    data object NavigateToCheckout : CartUiEffect
    data object NavigateToLogin : CartUiEffect
    data object NavigateToCatalog : CartUiEffect
    data class NavigateToProduct(val slug: String) : CartUiEffect
    data class ShowToast(val message: String) : CartUiEffect
}
