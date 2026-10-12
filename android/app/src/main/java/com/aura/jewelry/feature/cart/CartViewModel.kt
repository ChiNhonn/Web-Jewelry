package com.aura.jewelry.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.datastore.UserPreferencesRepository
import com.aura.jewelry.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<CartUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeUserSession()
        loadCart()
    }

    private fun observeUserSession() {
        viewModelScope.launch {
            userPreferencesRepository.userSession.collect { session ->
                _uiState.update { it.copy(isLoggedIn = session.isLoggedIn) }
            }
        }
    }

    fun onIntent(intent: CartUiIntent) {
        when (intent) {
            CartUiIntent.LoadCart -> loadCart()
            is CartUiIntent.UpdateQuantity -> updateQuantity(intent.itemId, intent.quantity)
            is CartUiIntent.RemoveItem -> removeItem(intent.itemId)
            CartUiIntent.ClearCart -> clearCart()
            is CartUiIntent.UpdateCouponCode -> {
                _uiState.update { it.copy(couponCode = intent.code, couponMessage = null) }
            }
            CartUiIntent.ApplyCoupon -> applyCoupon()
            is CartUiIntent.SetClearCartDialogVisible -> {
                _uiState.update { it.copy(showClearCartDialog = intent.visible) }
            }
            CartUiIntent.ProceedToCheckout -> proceedToCheckout()
            CartUiIntent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            cartRepository.getCart().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                cart = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    private fun updateQuantity(itemId: Long, quantity: Int) {
        if (quantity <= 0) {
            removeItem(itemId)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingItemId = itemId) }
            cartRepository.updateQuantity(itemId, quantity).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isUpdatingItemId = null,
                                cart = result.data
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isUpdatingItemId = null) }
                        _uiEffect.send(CartUiEffect.ShowToast(result.message))
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    private fun removeItem(itemId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingItemId = itemId) }
            cartRepository.removeItem(itemId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isUpdatingItemId = null,
                                cart = result.data
                            )
                        }
                        _uiEffect.send(CartUiEffect.ShowToast("Đã xóa sản phẩm khỏi túi đồ"))
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isUpdatingItemId = null) }
                        _uiEffect.send(CartUiEffect.ShowToast(result.message))
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showClearCartDialog = false) }
            cartRepository.clearCart().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                cart = it.cart?.copy(items = emptyList(), totalItems = 0, totalQuantity = 0, subtotal = 0.0, estimatedTotal = 0.0),
                                couponDiscount = 0.0,
                                couponApplied = false,
                                couponMessage = null
                            )
                        }
                        _uiEffect.send(CartUiEffect.ShowToast("Đã làm trống túi đồ"))
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _uiEffect.send(CartUiEffect.ShowToast(result.message))
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    private fun applyCoupon() {
        val code = _uiState.value.couponCode.trim().uppercase()
        val subtotal = _uiState.value.cart?.subtotal ?: 0.0

        if (code.isEmpty()) {
            _uiState.update { it.copy(couponMessage = "Vui lòng nhập mã ưu đãi") }
            return
        }

        when (code) {
            "VIPDIAMOND", "AURA2026" -> {
                val discount = subtotal * 0.10 // 10% discount
                _uiState.update {
                    it.copy(
                        couponDiscount = discount,
                        couponApplied = true,
                        couponMessage = "Đặc quyền VIP: Đã áp dụng giảm giá 10%!"
                    )
                }
            }
            "FREESHIP" -> {
                val shipping = _uiState.value.cart?.shippingFee ?: 0.0
                _uiState.update {
                    it.copy(
                        couponDiscount = shipping,
                        couponApplied = true,
                        couponMessage = "Đã áp dụng miễn phí vận chuyển bảo hiểm!"
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        couponDiscount = 0.0,
                        couponApplied = false,
                        couponMessage = "Mã ưu đãi '$code' không hợp lệ hoặc đã hết hạn"
                    )
                }
            }
        }
    }

    private fun proceedToCheckout() {
        val state = _uiState.value
        if (state.isCartEmpty) {
            return
        }

        viewModelScope.launch {
            if (state.isLoggedIn) {
                _uiEffect.send(CartUiEffect.NavigateToCheckout)
            } else {
                _uiEffect.send(CartUiEffect.ShowToast("Vui lòng đăng nhập để tiến hành đặt hàng an toàn"))
                _uiEffect.send(CartUiEffect.NavigateToLogin)
            }
        }
    }
}
