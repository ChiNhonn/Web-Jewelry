package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.OrderItemResponse;
import com.jewelry.store.dto.response.OrderResponse;
import com.jewelry.store.dto.response.OrderSummaryResponse;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.OrderItem;
import com.jewelry.store.entity.ProductImage;
import com.jewelry.store.entity.ProductVariant;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    default OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) return null;

        String thumbnailUrl = null;
        String productSlug = null;
        Long productId = null;

        if (item.getVariant() != null) {
            ProductVariant variant = item.getVariant();
            if (variant.getProduct() != null) {
                productId = variant.getProduct().getId();
                productSlug = variant.getProduct().getSlug();
                if (variant.getProduct().getImages() != null && !variant.getProduct().getImages().isEmpty()) {
                    thumbnailUrl = variant.getProduct().getImages().stream()
                            .filter(ProductImage::isThumbnail)
                            .findFirst()
                            .map(ProductImage::getImageUrl)
                            .orElse(variant.getProduct().getImages().get(0).getImageUrl());
                }
            }
        }

        return OrderItemResponse.builder()
                .id(item.getId())
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .productId(productId)
                .productName(item.getProductName())
                .productSlug(productSlug)
                .sku(item.getSku())
                .variantAttributes(item.getVariantAttributes())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    default OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        int totalQuantity = 0;

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                itemResponses.add(toOrderItemResponse(item));
                totalQuantity += item.getQuantity();
            }
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getUser() != null ? order.getUser().getFullName() : null)
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .customerPhone(order.getUser() != null ? order.getUser().getPhone() : null)
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPayment() != null ? order.getPayment().getPaymentMethod() : null)
                .transactionCode(order.getPayment() != null ? order.getPayment().getTransactionCode() : null)
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(order.getShippingFee())
                .totalAmount(order.getTotalAmount())
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .shippingAddressSnapshot(order.getShippingAddressSnapshot())
                .customerNote(order.getCustomerNote())
                .items(itemResponses)
                .totalQuantity(totalQuantity)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    default OrderSummaryResponse toOrderSummaryResponse(Order order) {
        if (order == null) return null;

        int itemCount = order.getOrderItems() != null ? order.getOrderItems().size() : 0;
        String firstItemName = null;
        String firstItemThumbnail = null;

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            OrderItem firstItem = order.getOrderItems().get(0);
            firstItemName = firstItem.getProductName();
            if (firstItem.getVariant() != null && firstItem.getVariant().getProduct() != null &&
                    firstItem.getVariant().getProduct().getImages() != null && !firstItem.getVariant().getProduct().getImages().isEmpty()) {
                firstItemThumbnail = firstItem.getVariant().getProduct().getImages().stream()
                        .filter(ProductImage::isThumbnail)
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(firstItem.getVariant().getProduct().getImages().get(0).getImageUrl());
            }
        }

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getUser() != null ? order.getUser().getFullName() : null)
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPayment() != null ? order.getPayment().getPaymentMethod() : null)
                .totalAmount(order.getTotalAmount())
                .itemCount(itemCount)
                .firstItemName(firstItemName)
                .firstItemThumbnail(firstItemThumbnail)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
