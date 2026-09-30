package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.AddToCartRequest;
import com.jewelry.store.dto.request.UpdateCartItemRequest;
import com.jewelry.store.dto.response.CartResponse;
import com.jewelry.store.entity.Cart;
import com.jewelry.store.entity.CartItem;
import com.jewelry.store.entity.ProductVariant;
import com.jewelry.store.entity.User;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.mapper.CartMapper;
import com.jewelry.store.repository.CartItemRepository;
import com.jewelry.store.repository.CartRepository;
import com.jewelry.store.repository.ProductVariantRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        log.info("User id {} adding variant id {} with quantity {}", userId, request.getVariantId(), request.getQuantity());

        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant", "id", request.getVariantId()));

        int availableStock = variant.getInventory() != null ? variant.getInventory().getAvailableQuantity() : 0;
        if (availableStock < request.getQuantity()) {
            throw new BadRequestException("Số lượng tồn kho không đủ để đáp ứng (Hiện chỉ còn " + availableStock + " sản phẩm).");
        }

        Cart cart = getOrCreateCart(userId);

        // Kiểm tra xem sản phẩm biến thể này đã có trong giỏ chưa
        CartItem existingItem = null;
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                if (item.getVariant().getId().equals(variant.getId())) {
                    existingItem = item;
                    break;
                }
            }
        }

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > availableStock) {
                throw new BadRequestException("Tổng số lượng trong giỏ (" + newQuantity + ") vượt quá số lượng tồn kho (" + availableStock + ").");
            }
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item", "id", cartItemId));

        // Kiểm tra bảo mật: Khách hàng chỉ được sửa giỏ hàng của chính mình
        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền sửa mặt hàng này.");
        }

        int availableStock = item.getVariant().getInventory() != null ? item.getVariant().getInventory().getAvailableQuantity() : 0;
        if (request.getQuantity() > availableStock) {
            throw new BadRequestException("Số lượng yêu cầu (" + request.getQuantity() + ") vượt quá tồn kho khả dụng (" + availableStock + ").");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        Cart cart = getOrCreateCart(userId);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item", "id", cartItemId));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xóa mặt hàng này.");
        }

        Cart cart = item.getCart();
        cart.removeItem(item);
        cartItemRepository.delete(item);

        Cart updatedCart = getOrCreateCart(userId);
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cart.clearItems();
            cartRepository.save(cart);
        }
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithDetails(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }
}
