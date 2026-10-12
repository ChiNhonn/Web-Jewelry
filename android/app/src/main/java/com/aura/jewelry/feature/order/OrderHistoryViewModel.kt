package com.aura.jewelry.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun onIntent(intent: OrderHistoryUiIntent) {
        when (intent) {
            is OrderHistoryUiIntent.FilterByStatus -> {
                _uiState.update { it.copy(selectedStatus = intent.status) }
                loadOrders()
            }
            is OrderHistoryUiIntent.Refresh -> {
                loadOrders(isRefresh = true)
            }
            is OrderHistoryUiIntent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun loadOrders(isRefresh: Boolean = false) {
        val statusParam = if (_uiState.value.selectedStatus == OrderStatusType.ALL) null else _uiState.value.selectedStatus.code

        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            orderRepository.getUserOrders(status = statusParam, page = 0, size = 30).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        // Handled by state flags
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                orders = result.data.content,
                                errorMessage = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}
