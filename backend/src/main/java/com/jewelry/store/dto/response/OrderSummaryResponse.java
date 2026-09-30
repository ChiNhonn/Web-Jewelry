package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {

    private Long id;
    private String orderCode;
    private Long userId;
    private String customerName;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private PaymentMethodType paymentMethod;

    private BigDecimal totalAmount;
    private int itemCount;
    private String firstItemName;
    private String firstItemThumbnail;

    private LocalDateTime createdAt;
}
