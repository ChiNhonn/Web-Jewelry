package com.aura.jewelry.core.network.api

import com.aura.jewelry.core.network.model.ApiResponseDto
import com.aura.jewelry.core.network.model.CancelOrderRequestDto
import com.aura.jewelry.core.network.model.CreateOrderRequestDto
import com.aura.jewelry.core.network.model.OrderResponseDto
import com.aura.jewelry.core.network.model.OrderSummaryDto
import com.aura.jewelry.core.network.model.PageResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApiService {

    @POST("api/v1/orders")
    suspend fun checkout(
        @Body request: CreateOrderRequestDto
    ): ApiResponseDto<OrderResponseDto>

    @GET("api/v1/orders")
    suspend fun getUserOrders(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): ApiResponseDto<PageResponseDto<OrderSummaryDto>>

    @GET("api/v1/orders/{orderCode}")
    suspend fun getOrderDetails(
        @Path("orderCode") orderCode: String
    ): ApiResponseDto<OrderResponseDto>

    @PUT("api/v1/orders/{orderCode}/cancel")
    suspend fun cancelOrder(
        @Path("orderCode") orderCode: String,
        @Body request: CancelOrderRequestDto? = null
    ): ApiResponseDto<OrderResponseDto>
}
