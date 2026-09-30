package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderCode;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private PaymentMethodType paymentMethod;
    private String transactionCode;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private String couponCode;
    private String shippingAddressSnapshot;
    private String customerNote;

    private List<OrderItemResponse> items;
    private int totalQuantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
