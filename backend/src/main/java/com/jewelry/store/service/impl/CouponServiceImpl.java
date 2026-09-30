package com.jewelry.store.service.impl;

import com.jewelry.store.dto.response.CouponResponse;
import com.jewelry.store.entity.Coupon;
import com.jewelry.store.entity.enums.DiscountType;
import com.jewelry.store.repository.CouponRepository;
import com.jewelry.store.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public CouponResponse validateCoupon(String code, BigDecimal orderSubtotal) {
        if (code == null || code.trim().isEmpty()) {
            return CouponResponse.builder()
                    .code(code)
                    .isValid(false)
                    .calculatedDiscount(BigDecimal.ZERO)
                    .message("Mã giảm giá không được để trống")
                    .build();
        }

        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCase(code.trim());
        if (couponOpt.isEmpty()) {
            return CouponResponse.builder()
                    .code(code)
                    .isValid(false)
                    .calculatedDiscount(BigDecimal.ZERO)
                    .message("Mã giảm giá không tồn tại")
                    .build();
        }

        Coupon coupon = couponOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (!coupon.isActive()) {
            return buildInvalidResponse(coupon, "Mã giảm giá này hiện đang tạm ngưng sử dụng");
        }
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return buildInvalidResponse(coupon, "Mã giảm giá chưa đến ngày áp dụng");
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            return buildInvalidResponse(coupon, "Mã giảm giá đã hết hạn sử dụng");
        }
        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return buildInvalidResponse(coupon, "Mã giảm giá đã đạt giới hạn số lần sử dụng");
        }
        if (coupon.getMinOrderValue() != null && orderSubtotal != null && orderSubtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            return buildInvalidResponse(coupon, "Đơn hàng chưa đạt giá trị tối thiểu " + coupon.getMinOrderValue() + "đ để dùng mã này");
        }

        BigDecimal calculatedDiscount = BigDecimal.ZERO;
        if (orderSubtotal != null && orderSubtotal.compareTo(BigDecimal.ZERO) > 0) {
            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                calculatedDiscount = orderSubtotal.multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (coupon.getMaxDiscount() != null && calculatedDiscount.compareTo(coupon.getMaxDiscount()) > 0) {
                    calculatedDiscount = coupon.getMaxDiscount();
                }
            } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                calculatedDiscount = coupon.getDiscountValue();
                if (calculatedDiscount.compareTo(orderSubtotal) > 0) {
                    calculatedDiscount = orderSubtotal;
                }
            }
        }

        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscount(coupon.getMaxDiscount())
                .isValid(true)
                .calculatedDiscount(calculatedDiscount)
                .message("Áp dụng mã giảm giá thành công! Giảm " + calculatedDiscount + "đ")
                .build();
    }

    private CouponResponse buildInvalidResponse(Coupon coupon, String message) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscount(coupon.getMaxDiscount())
                .isValid(false)
                .calculatedDiscount(BigDecimal.ZERO)
                .message(message)
                .build();
    }
}
