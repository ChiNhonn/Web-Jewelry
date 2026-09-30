package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.DiscountType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {

    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private boolean isValid;
    private BigDecimal calculatedDiscount;
    private String message;
}
