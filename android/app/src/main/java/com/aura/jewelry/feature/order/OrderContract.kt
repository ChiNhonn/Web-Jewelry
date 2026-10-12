package com.aura.jewelry.feature.order

import androidx.compose.ui.graphics.Color
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSakura
import com.aura.jewelry.core.network.model.OrderResponseDto
import com.aura.jewelry.core.network.model.OrderSummaryDto

enum class OrderStatusType(
    val code: String,
    val vietnameseTitle: String,
    val color: Color
) {
    ALL("ALL", "Tất cả", AuraGoldPrimary),
    PENDING("PENDING", "Chờ xác nhận", Color(0xFFFFA000)),
    CONFIRMED("CONFIRMED", "Đã xác nhận", Color(0xFF29B6F6)),
    PROCESSING("PROCESSING", "Đang gia công", Color(0xFFAB47BC)),
    SHIPPING("SHIPPING", "Đang vận chuyển", Color(0xFF26C6DA)),
    DELIVERED("DELIVERED", "Đã giao hàng", Color(0xFF66BB6A)),
    COMPLETED("COMPLETED", "Hoàn tất", Color(0xFF4CAF50)),
    CANCELLED("CANCELLED", "Đã hủy", AuraSakura);

    companion object {
        fun fromCode(code: String?): OrderStatusType {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: PENDING
        }
    }
}

data class OrderDetailUiState(
    val orderCode: String = "",
    val order: OrderResponseDto? = null,
    val isLoading: Boolean = false,
    val isCancelling: Boolean = false,
    val showCancelDialog: Boolean = false,
    val cancelReason: String = "",
    val errorMessage: String? = null
)

sealed interface OrderDetailUiIntent {
    data class LoadOrder(val orderCode: String) : OrderDetailUiIntent
    data object ShowCancelDialog : OrderDetailUiIntent
    data object DismissCancelDialog : OrderDetailUiIntent
    data class UpdateCancelReason(val reason: String) : OrderDetailUiIntent
    data object ConfirmCancelOrder : OrderDetailUiIntent
    data object DismissError : OrderDetailUiIntent
}

sealed interface OrderDetailUiEvent {
    data class ShowSnackbar(val message: String) : OrderDetailUiEvent
    data object OrderCancelledSuccess : OrderDetailUiEvent
}

data class OrderHistoryUiState(
    val selectedStatus: OrderStatusType = OrderStatusType.ALL,
    val orders: List<OrderSummaryDto> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

sealed interface OrderHistoryUiIntent {
    data class FilterByStatus(val status: OrderStatusType) : OrderHistoryUiIntent
    data object Refresh : OrderHistoryUiIntent
    data object DismissError : OrderHistoryUiIntent
}
