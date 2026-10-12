package com.aura.jewelry.feature.checkout

import com.aura.jewelry.core.network.model.CartResponseDto
import com.aura.jewelry.core.network.model.OrderResponseDto

enum class PaymentMethod(val code: String, val title: String, val description: String) {
    COD("COD", "Thanh toán khi nhận hàng (COD)", "Kiểm định chất lượng & ngọc kim hoàn trực tiếp trước khi thanh toán"),
    BANK_TRANSFER("BANK_TRANSFER", "Chuyển khoản trực tiếp (VietQR)", "Quét mã VietQR chuyển khoản nhanh 24/7 đến Techcombank / Vietcombank"),
    VNPAY("VNPAY", "Cổng thanh toán VNPAY-QR", "Hỗ trợ ứng dụng ngân hàng và thẻ ATM nội địa"),
    MOMO("MOMO", "Ví điện tử MoMo", "Thanh toán siêu tốc qua ứng dụng ví điện tử MoMo")
}

data class CheckoutUiState(
    val receiverName: String = "",
    val phone: String = "",
    val streetAddress: String = "",
    val ward: String = "",
    val district: String = "",
    val province: String = "",
    val customerNote: String = "",
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.COD,
    val cart: CartResponseDto? = null,
    val appliedCoupon: String? = null,
    val appliedDiscount: Double = 0.0,
    val isLoadingCart: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val orderSuccess: OrderResponseDto? = null,
    val errorMessage: String? = null,
    val nameError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val provinceError: String? = null
) {
    val subtotal: Double get() = cart?.subtotal ?: 0.0
    val shippingFee: Double get() = 0.0 // Miễn phí giao hàng bảo mật cao cấp
    val grandTotal: Double get() = maxOf(0.0, subtotal - appliedDiscount + shippingFee)
    val totalQuantity: Int get() = cart?.totalQuantity ?: 0
}

sealed interface CheckoutUiIntent {
    data class UpdateReceiverName(val name: String) : CheckoutUiIntent
    data class UpdatePhone(val phone: String) : CheckoutUiIntent
    data class UpdateStreetAddress(val address: String) : CheckoutUiIntent
    data class UpdateWard(val ward: String) : CheckoutUiIntent
    data class UpdateDistrict(val district: String) : CheckoutUiIntent
    data class UpdateProvince(val province: String) : CheckoutUiIntent
    data class UpdateCustomerNote(val note: String) : CheckoutUiIntent
    data class SelectPaymentMethod(val method: PaymentMethod) : CheckoutUiIntent
    data object PlaceOrder : CheckoutUiIntent
    data object DismissError : CheckoutUiIntent
    data object ResetSuccess : CheckoutUiIntent
}

sealed interface CheckoutUiEvent {
    data class OrderPlacedSuccess(val orderCode: String) : CheckoutUiEvent
    data class ShowSnackbar(val message: String) : CheckoutUiEvent
    data object NavigateBack : CheckoutUiEvent
}
