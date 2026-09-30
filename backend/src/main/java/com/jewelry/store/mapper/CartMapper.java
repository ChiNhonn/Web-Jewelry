package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.CartItemResponse;
import com.jewelry.store.dto.response.CartResponse;
import com.jewelry.store.entity.Cart;
import com.jewelry.store.entity.CartItem;
import com.jewelry.store.entity.ProductImage;
import com.jewelry.store.entity.ProductVariant;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    default CartItemResponse toCartItemResponse(CartItem item) {
        if (item == null) return null;

        ProductVariant variant = item.getVariant();
        BigDecimal unitPrice = variant.getSalePrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        int availableStock = 0;
        if (variant.getInventory() != null) {
            availableStock = variant.getInventory().getAvailableQuantity();
        }

        String thumbnailUrl = null;
        if (variant.getProduct() != null && variant.getProduct().getImages() != null && !variant.getProduct().getImages().isEmpty()) {
            thumbnailUrl = variant.getProduct().getImages().stream()
                    .filter(ProductImage::isThumbnail)
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(variant.getProduct().getImages().get(0).getImageUrl());
        }

        return CartItemResponse.builder()
                .id(item.getId())
                .variantId(variant.getId())
                .productId(variant.getProduct() != null ? variant.getProduct().getId() : null)
                .productName(variant.getProduct() != null ? variant.getProduct().getName() : "")
                .productSlug(variant.getProduct() != null ? variant.getProduct().getSlug() : "")
                .sku(variant.getSku())
                .sizeOption(variant.getSizeOption())
                .materialType(variant.getMaterialType())
                .unitPrice(unitPrice)
                .originalPrice(variant.getOriginalPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .thumbnailUrl(thumbnailUrl)
                .availableStock(availableStock)
                .inStock(availableStock >= item.getQuantity())
                .build();
    }

    default CartResponse toCartResponse(Cart cart) {
        if (cart == null) {
            return CartResponse.builder()
                    .items(new ArrayList<>())
                    .totalItems(0)
                    .totalQuantity(0)
                    .subtotal(BigDecimal.ZERO)
                    .shippingFee(BigDecimal.ZERO)
                    .estimatedTotal(BigDecimal.ZERO)
                    .build();
        }

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemResponse itemRes = toCartItemResponse(item);
                itemResponses.add(itemRes);
                subtotal = subtotal.add(itemRes.getSubtotal());
                totalQuantity += item.getQuantity();
            }
        }

        // Chính sách vận chuyển: Miễn phí nếu đơn hàng từ 10.000.000đ trở lên, ngược lại tính 50.000đ
        BigDecimal shippingFee = BigDecimal.ZERO;
        if (subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(new BigDecimal("10000000")) < 0) {
            shippingFee = new BigDecimal("50000");
        }

        BigDecimal estimatedTotal = subtotal.add(shippingFee);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalItems(itemResponses.size())
                .totalQuantity(totalQuantity)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .estimatedTotal(estimatedTotal)
                .build();
    }
}
