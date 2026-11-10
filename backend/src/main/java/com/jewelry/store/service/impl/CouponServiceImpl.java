package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateCouponRequest;
import com.jewelry.store.dto.request.UpdateCouponRequest;
import com.jewelry.store.dto.response.AdminCouponResponse;
import com.jewelry.store.dto.response.CouponResponse;
import com.jewelry.store.entity.Coupon;
import com.jewelry.store.entity.enums.DiscountType;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.CouponRepository;
import com.jewelry.store.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public CouponResponse validateCoupon(String code, BigDecimal orderSubtotal) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code == null ? "" : code.trim()).orElse(null);
        BigDecimal amount = orderSubtotal == null ? BigDecimal.ZERO : orderSubtotal;
        if (coupon == null || !coupon.isActive()) {
            return invalid(code, "Mã giảm giá không tồn tại hoặc đã tắt");
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate().isAfter(now) || coupon.getEndDate().isBefore(now)) {
            return invalid(code, "Mã giảm giá chưa đến hạn hoặc đã hết hạn");
        }
        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return invalid(code, "Mã giảm giá đã hết lượt dùng");
        }
        if (amount.compareTo(coupon.getMinOrderAmount()) < 0) {
            return invalid(code, "Đơn hàng chưa đạt giá trị tối thiểu");
        }
        BigDecimal discount = discountOf(coupon, amount);
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(typeOf(coupon.getDiscountType()))
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderAmount())
                .maxDiscount(coupon.getMaxDiscountAmount())
                .isValid(true)
                .calculatedDiscount(discount)
                .message("Áp dụng mã giảm giá thành công")
                .build();
    }

    @Override
    @Transactional
    public AdminCouponResponse createCoupon(CreateCouponRequest request) {
        Coupon coupon = couponRepository.save(Coupon.builder()
                .code(request.getCode().trim().toUpperCase())
                .title(request.getCode().trim().toUpperCase())
                .discountType(request.getDiscountType().name())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderValue() == null ? BigDecimal.ZERO : request.getMinOrderValue())
                .maxDiscountAmount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.isActive())
                .createdAt(LocalDateTime.now())
                .build());
        return toAdmin(coupon);
    }

    @Override
    @Transactional
    public AdminCouponResponse updateCoupon(Long id, UpdateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã giảm giá"));
        if (request.getDiscountType() != null) {
            coupon.setDiscountType(request.getDiscountType().name());
        }
        if (request.getDiscountValue() != null) {
            coupon.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMinOrderValue() != null) {
            coupon.setMinOrderAmount(request.getMinOrderValue());
        }
        if (request.getMaxDiscount() != null) {
            coupon.setMaxDiscountAmount(request.getMaxDiscount());
        }
        if (request.getUsageLimit() != null) {
            coupon.setUsageLimit(request.getUsageLimit());
        }
        if (request.getStartDate() != null) {
            coupon.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            coupon.setEndDate(request.getEndDate());
        }
        if (request.getIsActive() != null) {
            coupon.setActive(request.getIsActive());
        }
        return toAdmin(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCouponResponse getCouponById(Long id) {
        return toAdmin(couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã giảm giá")));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminCouponResponse> getAllCoupons(Boolean activeOnly, int page, int size) {
        List<AdminCouponResponse> all = couponRepository.findAll().stream()
                .filter(coupon -> activeOnly == null || !activeOnly || coupon.isActive())
                .sorted(Comparator.comparing(Coupon::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toAdmin)
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        int pages = (int) Math.ceil(all.size() / (double) safeSize);
        return PageResponse.<AdminCouponResponse>builder()
                .content(all.subList(from, to))
                .page(safePage)
                .size(safeSize)
                .totalElements(all.size())
                .totalPages(Math.max(pages, 1))
                .first(safePage == 0)
                .last(safePage >= pages - 1)
                .build();
    }

    private CouponResponse invalid(String code, String message) {
        return CouponResponse.builder()
                .code(code)
                .isValid(false)
                .calculatedDiscount(BigDecimal.ZERO)
                .message(message)
                .build();
    }

    private BigDecimal discountOf(Coupon coupon, BigDecimal amount) {
        BigDecimal discount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = amount.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null) {
                discount = discount.min(coupon.getMaxDiscountAmount());
            }
        } else {
            discount = coupon.getDiscountValue();
        }
        return discount.min(amount).max(BigDecimal.ZERO);
    }

    private AdminCouponResponse toAdmin(Coupon coupon) {
        return AdminCouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .title(coupon.getTitle())
                .discountType(typeOf(coupon.getDiscountType()))
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderAmount())
                .maxDiscount(coupon.getMaxDiscountAmount())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .isActive(coupon.isActive())
                .isExpired(coupon.getEndDate() != null && coupon.getEndDate().isBefore(LocalDateTime.now()))
                .createdAt(coupon.getCreatedAt())
                .build();
    }

    private DiscountType typeOf(String value) {
        return "FIXED_AMOUNT".equalsIgnoreCase(value) ? DiscountType.FIXED_AMOUNT : DiscountType.PERCENTAGE;
    }
}
