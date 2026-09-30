package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 3, max = 50, message = "Mã voucher từ 3 đến 50 ký tự")
    private String code;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Positive(message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal discountValue;

    @PositiveOrZero(message = "Đơn hàng tối thiểu không được âm")
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @PositiveOrZero(message = "Mức giảm tối đa không được âm")
    private BigDecimal maxDiscount;

    @Min(value = 1, message = "Số lượt dùng tối thiểu là 1")
    @Builder.Default
    private int usageLimit = 100;

    @NotNull(message = "Ngày bắt đầu áp dụng không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    @Builder.Default
    private boolean isActive = true;
}
