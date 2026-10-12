package com.aura.jewelry.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<OrderDetailUiEvent>()
    val uiEvent: SharedFlow<OrderDetailUiEvent> = _uiEvent.asSharedFlow()

    fun onIntent(intent: OrderDetailUiIntent) {
        when (intent) {
            is OrderDetailUiIntent.LoadOrder -> {
                loadOrder(intent.orderCode)
            }
            is OrderDetailUiIntent.ShowCancelDialog -> {
                _uiState.update { it.copy(showCancelDialog = true) }
            }
            is OrderDetailUiIntent.DismissCancelDialog -> {
                _uiState.update { it.copy(showCancelDialog = false) }
            }
            is OrderDetailUiIntent.UpdateCancelReason -> {
                _uiState.update { it.copy(cancelReason = intent.reason) }
            }
            is OrderDetailUiIntent.ConfirmCancelOrder -> {
                confirmCancelOrder()
            }
            is OrderDetailUiIntent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun loadOrder(orderCode: String) {
        _uiState.update { it.copy(orderCode = orderCode, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            orderRepository.getOrderDetails(orderCode).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                order = result.data,
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
                        _uiEvent.emit(OrderDetailUiEvent.ShowSnackbar(result.message))
                    }
                }
            }
        }
    }

    private fun confirmCancelOrder() {
        val currentOrderCode = _uiState.value.orderCode
        val reason = _uiState.value.cancelReason.ifBlank { "Khách hàng yêu cầu hủy đơn qua ứng dụng di động" }

        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true, showCancelDialog = false) }
            orderRepository.cancelOrder(currentOrderCode, reason).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isCancelling = true) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isCancelling = false,
                                order = result.data,
                                errorMessage = null
                            )
                        }
                        _uiEvent.emit(OrderDetailUiEvent.ShowSnackbar("Hủy đơn hàng thành công"))
                        _uiEvent.emit(OrderDetailUiEvent.OrderCancelledSuccess)
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isCancelling = false,
                                errorMessage = result.message
                            )
                        }
                        _uiEvent.emit(OrderDetailUiEvent.ShowSnackbar(result.message))
                    }
                }
            }
        }
    }
}
