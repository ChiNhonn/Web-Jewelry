package com.jewelry.store.service;

import com.jewelry.store.dto.response.CouponResponse;

import java.math.BigDecimal;

public interface CouponService {

    CouponResponse validateCoupon(String code, BigDecimal orderSubtotal);
}
