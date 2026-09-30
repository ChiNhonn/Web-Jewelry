package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.response.PaymentInitiateResponse;
import com.jewelry.store.dto.response.VNPayReturnResponse;
import com.jewelry.store.security.UserPrincipal;
import com.jewelry.store.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Gateways", description = "Endpoints for initiating and processing VNPay, VietQR, and COD payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderCode}/initiate")
    @Operation(summary = "Initiate payment for order", description = "Khởi tạo thanh toán: sinh URL thanh toán VNPay Sandbox hoặc tạo mã VietQR chuyển khoản")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<ApiResponse<PaymentInitiateResponse>> initiatePayment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String orderCode,
            HttpServletRequest request) {
        PaymentInitiateResponse response = paymentService.initiatePayment(currentUser.getId(), orderCode, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{orderCode}")
    @Operation(summary = "Get payment details for order", description = "Xem chi tiết trạng thái thanh toán và thông tin chuyển khoản VietQR")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<ApiResponse<PaymentInitiateResponse>> getPaymentDetails(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String orderCode) {
        PaymentInitiateResponse response = paymentService.getPaymentDetails(currentUser.getId(), orderCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vnpay-return")
    @Operation(summary = "VNPay return callback", description = "Endpoint tiếp nhận redirect từ cổng thanh toán VNPay sau khi khách hàng giao dịch")
    public ResponseEntity<ApiResponse<VNPayReturnResponse>> vnpayReturn(
            @RequestParam Map<String, String> queryParams) {
        VNPayReturnResponse response = paymentService.processVNPayReturn(queryParams);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vnpay-ipn")
    @Operation(summary = "VNPay Instant Payment Notification (IPN)", description = "Webhook nhận thông báo kết quả giao dịch ngầm bất đồng bộ từ máy chủ VNPay")
    public ResponseEntity<Map<String, String>> vnpayIpn(
            @RequestParam Map<String, String> queryParams) {
        Map<String, String> response = paymentService.processVNPayIpn(queryParams);
        return ResponseEntity.ok(response);
    }
}
