package com.jewelry.store.service;

import com.jewelry.store.dto.request.AddToCartRequest;
import com.jewelry.store.dto.request.UpdateCartItemRequest;
import com.jewelry.store.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addToCart(Long userId, AddToCartRequest request);

    CartResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long userId, Long cartItemId);

    void clearCart(Long userId);
}
