package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateResponse {

    private String orderCode;
    private PaymentMethodType paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private String paymentUrl;
    private String message;
}
