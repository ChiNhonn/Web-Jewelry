package com.aura.jewelry.core.network.api

import com.aura.jewelry.core.network.model.AddToCartRequestDto
import com.aura.jewelry.core.network.model.ApiResponseDto
import com.aura.jewelry.core.network.model.CartResponseDto
import com.aura.jewelry.core.network.model.UpdateCartItemRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApiService {

    @GET("api/v1/cart")
    suspend fun getCart(): ApiResponseDto<CartResponseDto>

    @POST("api/v1/cart/items")
    suspend fun addToCart(
        @Body request: AddToCartRequestDto
    ): ApiResponseDto<CartResponseDto>

    @PUT("api/v1/cart/items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: Long,
        @Body request: UpdateCartItemRequestDto
    ): ApiResponseDto<CartResponseDto>

    @DELETE("api/v1/cart/items/{itemId}")
    suspend fun removeCartItem(
        @Path("itemId") itemId: Long
    ): ApiResponseDto<CartResponseDto>

    @DELETE("api/v1/cart")
    suspend fun clearCart(): ApiResponseDto<Unit?>
}
