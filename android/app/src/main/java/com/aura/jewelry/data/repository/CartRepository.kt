package com.aura.jewelry.data.repository

import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.common.safeApiCall
import com.aura.jewelry.core.network.api.CartApiService
import com.aura.jewelry.core.network.model.AddToCartRequestDto
import com.aura.jewelry.core.network.model.CartResponseDto
import com.aura.jewelry.core.network.model.UpdateCartItemRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface CartRepository {
    fun getCart(): Flow<NetworkResult<CartResponseDto>>
    fun addToCart(variantId: Long, quantity: Int = 1): Flow<NetworkResult<CartResponseDto>>
    fun updateQuantity(itemId: Long, quantity: Int): Flow<NetworkResult<CartResponseDto>>
    fun removeItem(itemId: Long): Flow<NetworkResult<CartResponseDto>>
    fun clearCart(): Flow<NetworkResult<Unit>>
}

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartApiService: CartApiService
) : CartRepository {

    override fun getCart(): Flow<NetworkResult<CartResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            cartApiService.getCart()
        }
        emit(result)
    }

    override fun addToCart(variantId: Long, quantity: Int): Flow<NetworkResult<CartResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            cartApiService.addToCart(AddToCartRequestDto(variantId = variantId, quantity = quantity))
        }
        emit(result)
    }

    override fun updateQuantity(itemId: Long, quantity: Int): Flow<NetworkResult<CartResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            cartApiService.updateCartItem(itemId, UpdateCartItemRequestDto(quantity = quantity))
        }
        emit(result)
    }

    override fun removeItem(itemId: Long): Flow<NetworkResult<CartResponseDto>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            cartApiService.removeCartItem(itemId)
        }
        emit(result)
    }

    override fun clearCart(): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        val result = safeApiCall {
            cartApiService.clearCart()
        }
        if (result is NetworkResult.Success) {
            emit(NetworkResult.Success(Unit, "Đã làm trống giỏ hàng"))
        } else if (result is NetworkResult.Error) {
            emit(result)
        }
    }
}
