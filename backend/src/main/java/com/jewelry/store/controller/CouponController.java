package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.response.CouponResponse;
import com.jewelry.store.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons & Discounts", description = "Endpoints for checking and previewing discount vouchers")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/validate")
    @Operation(summary = "Validate coupon code", description = "Kiểm tra tính hợp lệ và xem trước số tiền giảm giá của mã voucher")
    public ResponseEntity<ApiResponse<CouponResponse>> validateCoupon(
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "0") BigDecimal amount) {
        CouponResponse response = couponService.validateCoupon(code, amount);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
