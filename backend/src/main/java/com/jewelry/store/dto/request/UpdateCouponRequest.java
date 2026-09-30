package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.DiscountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCouponRequest {

    private DiscountType discountType;

    @Positive(message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal discountValue;

    @PositiveOrZero(message = "Đơn hàng tối thiểu không được âm")
    private BigDecimal minOrderValue;

    @PositiveOrZero(message = "Mức giảm tối đa không được âm")
    private BigDecimal maxDiscount;

    @Min(value = 1, message = "Số lượt dùng tối thiểu là 1")
    private Integer usageLimit;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
}
