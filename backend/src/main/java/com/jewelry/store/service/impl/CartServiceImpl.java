package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.AddToCartRequest;
import com.jewelry.store.dto.request.UpdateCartItemRequest;
import com.jewelry.store.dto.response.CartItemResponse;
import com.jewelry.store.dto.response.CartResponse;
import com.jewelry.store.entity.CartItem;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.User;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.CartItemRepository;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.CartService;
import com.jewelry.store.service.PriceBook;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PriceBook priceBook;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return toResponse(userId, cartItemRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Product product = productRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, product.getId())
                .orElseGet(() -> CartItem.builder()
                        .user(userRepository.findById(userId).orElseThrow())
                        .product(product)
                        .quantity(0)
                        .build());
        int next = item.getQuantity() + request.getQuantity();
        if (next > product.getQuantity()) {
            throw new BadRequestException("Chỉ còn " + product.getQuantity() + " sản phẩm");
        }
        item.setQuantity(next);
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng giỏ hàng"));
        if (!item.getUser().getId().equals(userId)) {
            throw new BadRequestException("Không có quyền sửa giỏ hàng này");
        }
        if (request.getQuantity() > item.getProduct().getQuantity()) {
            throw new BadRequestException("Không đủ hàng");
        }
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng giỏ hàng"));
        if (!item.getUser().getId().equals(userId)) {
            throw new BadRequestException("Không có quyền sửa giỏ hàng này");
        }
        cartItemRepository.delete(item);
        return getCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public CartResponse mergeItems(Long userId, List<AddToCartRequest> items) {
        if (items != null) {
            for (AddToCartRequest item : items) {
                addToCart(userId, item);
            }
        }
        return getCart(userId);
    }

    private CartResponse toResponse(Long userId, List<CartItem> items) {
        List<CartItemResponse> lines = items.stream().map(item -> {
            PriceBook.Quote quote = priceBook.quote(item.getProduct());
            int stock = item.getProduct().getQuantity() == null ? 0 : item.getProduct().getQuantity();
            return CartItemResponse.builder()
                    .id(item.getId())
                    .variantId(item.getProduct().getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productSlug(StoreText.productSlug(item.getProduct().getId()))
                    .sku("SP-" + item.getProduct().getId())
                    .sizeOption(item.getProduct().getSize())
                    .materialType(item.getProduct().getMaterial())
                    .unitPrice(quote.sale())
                    .originalPrice(quote.original())
                    .quantity(item.getQuantity())
                    .subtotal(quote.sale().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .thumbnailUrl(StoreText.imageUrl(item.getProduct().getImageName()))
                    .availableStock(stock)
                    .inStock(stock >= item.getQuantity())
                    .build();
        }).toList();
        BigDecimal subtotal = lines.stream().map(CartItemResponse::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartResponse.builder()
                .id(userId)
                .items(lines)
                .totalItems(lines.size())
                .totalQuantity(lines.stream().mapToInt(CartItemResponse::getQuantity).sum())
                .subtotal(subtotal)
                .shippingFee(BigDecimal.ZERO)
                .estimatedTotal(subtotal)
                .build();
    }
}
