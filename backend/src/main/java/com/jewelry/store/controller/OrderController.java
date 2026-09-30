package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CancelOrderRequest;
import com.jewelry.store.dto.request.CreateOrderRequest;
import com.jewelry.store.dto.response.OrderResponse;
import com.jewelry.store.dto.response.OrderSummaryResponse;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.security.UserPrincipal;
import com.jewelry.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Customer Orders", description = "Endpoints for order checkout, order history and customer cancellations")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Checkout & place order", description = "Tạo đơn hàng từ giỏ hàng hiện tại với bảo mật giá Zero-Trust và kiểm soát kho nguyên tử")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(currentUser.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Đặt hàng thành công! Mã đơn: " + response.getOrderCode(), response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get user order history", description = "Lấy danh sách lịch sử đơn hàng của người dùng có phân trang và lọc theo trạng thái")
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderSummaryResponse> response = orderService.getUserOrders(currentUser.getId(), status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{orderCode}")
    @Operation(summary = "Get order details", description = "Xem chi tiết một đơn hàng theo mã đơn")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String orderCode) {
        OrderResponse response = orderService.getOrderByCode(currentUser.getId(), orderCode, false);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{orderCode}/cancel")
    @Operation(summary = "Cancel order", description = "Khách hàng hủy đơn hàng khi trạng thái còn đang chờ xử lý (PENDING)")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String orderCode,
            @RequestBody(required = false) CancelOrderRequest request) {
        OrderResponse response = orderService.cancelOrder(currentUser.getId(), orderCode, request, false);
        return ResponseEntity.ok(ApiResponse.success("Hủy đơn hàng thành công!", response));
    }
}
