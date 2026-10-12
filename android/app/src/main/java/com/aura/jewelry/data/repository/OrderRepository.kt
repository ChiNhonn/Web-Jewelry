package com.aura.jewelry.data.repository

import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.common.safeApiCall
import com.aura.jewelry.core.network.api.OrderApiService
import com.aura.jewelry.core.network.model.CancelOrderRequestDto
import com.aura.jewelry.core.network.model.CreateOrderRequestDto
import com.aura.jewelry.core.network.model.OrderResponseDto
import com.aura.jewelry.core.network.model.OrderSummaryDto
import com.aura.jewelry.core.network.model.PageResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface OrderRepository {
    fun checkout(request: CreateOrderRequestDto): Flow<NetworkResult<OrderResponseDto>>
    fun getUserOrders(status: String? = null, page: Int = 0, size: Int = 10): Flow<NetworkResult<PageResponseDto<OrderSummaryDto>>>
    fun getOrderDetails(orderCode: String): Flow<NetworkResult<OrderResponseDto>>
    fun cancelOrder(orderCode: String, reason: String? = null): Flow<NetworkResult<OrderResponseDto>>
}

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderApiService: OrderApiService
) : OrderRepository {

    override fun checkout(request: CreateOrderRequestDto): Flow<NetworkResult<OrderResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            orderApiService.checkout(request)
        }
        emit(result)
    }

    override fun getUserOrders(status: String?, page: Int, size: Int): Flow<NetworkResult<PageResponseDto<OrderSummaryDto>>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            orderApiService.getUserOrders(status = status, page = page, size = size)
        }
        emit(result)
    }

    override fun getOrderDetails(orderCode: String): Flow<NetworkResult<OrderResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            orderApiService.getOrderDetails(orderCode)
        }
        emit(result)
    }

    override fun cancelOrder(orderCode: String, reason: String?): Flow<NetworkResult<OrderResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            orderApiService.cancelOrder(
                orderCode = orderCode,
                request = CancelOrderRequestDto(reason = reason ?: "Khách hàng yêu cầu hủy đơn")
            )
        }
        emit(result)
    }
}
