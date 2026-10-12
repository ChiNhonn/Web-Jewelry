package com.aura.jewelry.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.datastore.UserPreferencesRepository
import com.aura.jewelry.core.network.model.CreateOrderRequestDto
import com.aura.jewelry.core.network.model.ShippingAddressDto
import com.aura.jewelry.data.repository.CartRepository
import com.aura.jewelry.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CheckoutUiEvent>()
    val uiEvent: SharedFlow<CheckoutUiEvent> = _uiEvent.asSharedFlow()

    private val phoneRegex = Regex("^(0|\\+84)[3|5|7|8|9][0-9]{8}$")

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load user profile name if logged in
            val session = userPreferencesRepository.userSession.firstOrNull()
            if (session != null && !session.fullName.isNullOrBlank()) {
                _uiState.update { it.copy(receiverName = session.fullName) }
            }

            // Load current cart to verify items and total
            _uiState.update { it.copy(isLoadingCart = true) }
            cartRepository.getCart().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoadingCart = false,
                                cart = result.data
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingCart = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoadingCart = true) }
                    }
                }
            }
        }
    }

    fun onIntent(intent: CheckoutUiIntent) {
        when (intent) {
            is CheckoutUiIntent.UpdateReceiverName -> {
                _uiState.update {
                    it.copy(
                        receiverName = intent.name,
                        nameError = if (intent.name.isBlank()) "Vui lòng nhập họ và tên người nhận" else null
                    )
                }
            }
            is CheckoutUiIntent.UpdatePhone -> {
                val cleanPhone = intent.phone.filter { it.isDigit() || it == '+' }
                val error = when {
                    cleanPhone.isBlank() -> "Vui lòng nhập số điện thoại"
                    !cleanPhone.matches(phoneRegex) -> "Số điện thoại Việt Nam không hợp lệ (10 số)"
                    else -> null
                }
                _uiState.update {
                    it.copy(
                        phone = cleanPhone,
                        phoneError = error
                    )
                }
            }
            is CheckoutUiIntent.UpdateStreetAddress -> {
                _uiState.update {
                    it.copy(
                        streetAddress = intent.address,
                        addressError = if (intent.address.isBlank()) "Vui lòng nhập số nhà, tên đường" else null
                    )
                }
            }
            is CheckoutUiIntent.UpdateWard -> {
                _uiState.update { it.copy(ward = intent.ward) }
            }
            is CheckoutUiIntent.UpdateDistrict -> {
                _uiState.update { it.copy(district = intent.district) }
            }
            is CheckoutUiIntent.UpdateProvince -> {
                _uiState.update {
                    it.copy(
                        province = intent.province,
                        provinceError = if (intent.province.isBlank()) "Vui lòng chọn hoặc nhập Tỉnh/Thành phố" else null
                    )
                }
            }
            is CheckoutUiIntent.UpdateCustomerNote -> {
                _uiState.update { it.copy(customerNote = intent.note) }
            }
            is CheckoutUiIntent.SelectPaymentMethod -> {
                _uiState.update { it.copy(selectedPaymentMethod = intent.method) }
            }
            is CheckoutUiIntent.PlaceOrder -> {
                placeOrder()
            }
            is CheckoutUiIntent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            is CheckoutUiIntent.ResetSuccess -> {
                _uiState.update { it.copy(orderSuccess = null) }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        val nameErr = if (state.receiverName.isBlank()) "Vui lòng nhập họ và tên người nhận" else null
        val phoneErr = when {
            state.phone.isBlank() -> "Vui lòng nhập số điện thoại"
            !state.phone.matches(phoneRegex) -> "Số điện thoại Việt Nam không hợp lệ (ví dụ: 0912345678)"
            else -> null
        }
        val addressErr = if (state.streetAddress.isBlank()) "Vui lòng nhập địa chỉ chi tiết" else null
        val provinceErr = if (state.province.isBlank()) "Vui lòng nhập Tỉnh/Thành phố" else null

        _uiState.update {
            it.copy(
                nameError = nameErr,
                phoneError = phoneErr,
                addressError = addressErr,
                provinceError = provinceErr
            )
        }

        return nameErr == null && phoneErr == null && addressErr == null && provinceErr == null
    }

    private fun placeOrder() {
        if (!validateForm()) {
            viewModelScope.launch {
                _uiEvent.emit(CheckoutUiEvent.ShowSnackbar("Vui lòng kiểm tra lại thông tin nhận hàng"))
            }
            return
        }

        val state = _uiState.value
        val itemsCount = state.cart?.items?.size ?: 0
        if (itemsCount == 0) {
            viewModelScope.launch {
                _uiEvent.emit(CheckoutUiEvent.ShowSnackbar("Giỏ hàng của bạn đang trống"))
            }
            return
        }

        viewModelScope.launch {
            val shippingAddress = ShippingAddressDto(
                receiverName = state.receiverName.trim(),
                phone = state.phone.trim(),
                streetAddress = state.streetAddress.trim(),
                ward = state.ward.trim(),
                district = state.district.trim(),
                province = state.province.trim()
            )

            val request = CreateOrderRequestDto(
                shippingAddress = shippingAddress,
                paymentMethod = state.selectedPaymentMethod.code,
                couponCode = state.appliedCoupon,
                customerNote = state.customerNote.trim().ifBlank { null }
            )

            orderRepository.checkout(request).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isPlacingOrder = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isPlacingOrder = false,
                                orderSuccess = result.data,
                                errorMessage = null
                            )
                        }
                        // Fire one-time event for UI
                        result.data.orderCode.let { code ->
                            _uiEvent.emit(CheckoutUiEvent.OrderPlacedSuccess(code))
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isPlacingOrder = false,
                                errorMessage = result.message
                            )
                        }
                        _uiEvent.emit(CheckoutUiEvent.ShowSnackbar(result.message))
                    }
                }
            }
        }
    }
}
