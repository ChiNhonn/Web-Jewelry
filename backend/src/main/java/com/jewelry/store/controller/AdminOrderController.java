package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CancelOrderRequest;
import com.jewelry.store.dto.request.UpdateOrderStatusRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
@Tag(name = "Admin Order Management", description = "Endpoints for managing store orders, updating statuses, and inventory tracking")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "List all orders", description = "Lấy danh sách tất cả đơn hàng kèm phân trang, lọc theo trạng thái và tìm kiếm từ khóa")
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderSummaryResponse> response = orderService.getAllOrdersForAdmin(status, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{orderCode}")
    @Operation(summary = "Get order details by admin", description = "Xem toàn bộ thông tin chi tiết đơn hàng cho nhân viên/quản trị viên")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            @PathVariable String orderCode) {
        OrderResponse response = orderService.getOrderByCode(null, orderCode, true);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{orderCode}/status")
    @Operation(summary = "Update order status", description = "Cập nhật tiến trình trạng thái đơn hàng (Xác nhận, chuẩn bị hàng, đang giao, đã giao)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String orderCode,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatusByAdmin(orderCode, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn hàng thành công!", response));
    }

    @PutMapping("/{orderCode}/cancel")
    @Operation(summary = "Cancel order by admin", description = "Hủy đơn hàng bởi quản trị viên và tự động hoàn lại số lượng tồn kho")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrderByAdmin(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String orderCode,
            @RequestBody(required = false) CancelOrderRequest request) {
        OrderResponse response = orderService.cancelOrder(currentUser.getId(), orderCode, request, true);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy đơn hàng thành công!", response));
    }
}
