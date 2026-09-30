package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateCouponRequest;
import com.jewelry.store.dto.request.UpdateCouponRequest;
import com.jewelry.store.dto.response.AdminCouponResponse;
import com.jewelry.store.dto.response.CouponResponse;
import com.jewelry.store.entity.Coupon;
import com.jewelry.store.entity.enums.DiscountType;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.CouponRepository;
import com.jewelry.store.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    @Transactional
    public AdminCouponResponse createCoupon(CreateCouponRequest request) {
        String cleanCode = request.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(cleanCode)) {
            throw new BadRequestException("Mã voucher '" + cleanCode + "' đã tồn tại.");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc.");
        }

        Coupon coupon = Coupon.builder()
                .code(cleanCode)
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : BigDecimal.ZERO)
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.isActive())
                .build();

        Coupon saved = couponRepository.save(coupon);
        log.info("Created new coupon with code: {}", saved.getCode());
        return mapToAdminCouponResponse(saved);
    }

    @Override
    @Transactional
    public AdminCouponResponse updateCoupon(Long id, UpdateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));

        if (request.getDiscountType() != null) coupon.setDiscountType(request.getDiscountType());
        if (request.getDiscountValue() != null) coupon.setDiscountValue(request.getDiscountValue());
        if (request.getMinOrderValue() != null) coupon.setMinOrderValue(request.getMinOrderValue());
        if (request.getMaxDiscount() != null) coupon.setMaxDiscount(request.getMaxDiscount());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getStartDate() != null) coupon.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) coupon.setEndDate(request.getEndDate());
        if (request.getIsActive() != null) coupon.setActive(request.getIsActive());

        if (coupon.getStartDate().isAfter(coupon.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu không được sau ngày kết thúc.");
        }

        Coupon saved = couponRepository.save(coupon);
        log.info("Updated coupon id: {}", id);
        return mapToAdminCouponResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
        coupon.setActive(false);
        couponRepository.save(coupon);
        log.info("Deactivated coupon id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCouponResponse getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
        return mapToAdminCouponResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminCouponResponse> getAllCoupons(Boolean activeOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Coupon> couponPage;
        if (Boolean.TRUE.equals(activeOnly)) {
            couponPage = couponRepository.findByIsActive(true, pageable);
        } else {
            couponPage = couponRepository.findAll(pageable);
        }
        Page<AdminCouponResponse> responsePage = couponPage.map(this::mapToAdminCouponResponse);
        return PageResponse.from(responsePage);
    }

    private AdminCouponResponse mapToAdminCouponResponse(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        boolean isExpired = (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate()))
                || (coupon.getUsedCount() >= coupon.getUsageLimit());

        return AdminCouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .isActive(coupon.isActive())
                .isExpired(isExpired)
                .createdAt(coupon.getCreatedAt())
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
