package com.jewelry.store.controller.admin;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateCouponRequest;
import com.jewelry.store.dto.request.UpdateCouponRequest;
import com.jewelry.store.dto.response.AdminCouponResponse;
import com.jewelry.store.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Coupon & Promotions Management", description = "Endpoints for creating and administering discount codes and promotional vouchers")
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "Get paginated coupons list", description = "Lấy danh sách mã giảm giá kèm phân trang và lọc theo trạng thái hiệu lực")
    public ResponseEntity<ApiResponse<PageResponse<AdminCouponResponse>>> getAllCoupons(
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminCouponResponse> response = couponService.getAllCoupons(activeOnly, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get coupon detail by ID", description = "Xem thông tin chi tiết cấu hình mã voucher")
    public ResponseEntity<ApiResponse<AdminCouponResponse>> getCouponById(@PathVariable Long id) {
        AdminCouponResponse response = couponService.getCouponById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create promotional coupon", description = "Tạo mã giảm giá mới với điều kiện đơn tối thiểu và giới hạn số lượt dùng")
    public ResponseEntity<ApiResponse<AdminCouponResponse>> createCoupon(
            @Valid @RequestBody CreateCouponRequest request) {
        AdminCouponResponse response = couponService.createCoupon(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo mã voucher thành công!", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update promotional coupon", description = "Cập nhật thông tin mã giảm giá")
    public ResponseEntity<ApiResponse<AdminCouponResponse>> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCouponRequest request) {
        AdminCouponResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật mã voucher thành công!", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate coupon", description = "Ngưng kích hoạt mã voucher")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.empty("Đã vô hiệu hóa mã voucher thành công!"));
    }
}
