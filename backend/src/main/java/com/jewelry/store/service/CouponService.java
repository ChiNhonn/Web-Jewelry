package com.jewelry.store.service;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateCouponRequest;
import com.jewelry.store.dto.request.UpdateCouponRequest;
import com.jewelry.store.dto.response.AdminCouponResponse;
import com.jewelry.store.dto.response.CouponResponse;

import java.math.BigDecimal;

public interface CouponService {

    CouponResponse validateCoupon(String code, BigDecimal orderSubtotal);

    AdminCouponResponse createCoupon(CreateCouponRequest request);

    AdminCouponResponse updateCoupon(Long id, UpdateCouponRequest request);

    void deleteCoupon(Long id);

    AdminCouponResponse getCouponById(Long id);

    PageResponse<AdminCouponResponse> getAllCoupons(Boolean activeOnly, int page, int size);
}
