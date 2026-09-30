package com.jewelry.store.controller.admin;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.StockAdjustmentRequest;
import com.jewelry.store.dto.request.UpdateThresholdRequest;
import com.jewelry.store.dto.response.InventoryDetailResponse;
import com.jewelry.store.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
@Tag(name = "Admin Inventory & Warehouse Management", description = "Endpoints for stock replenishment, inventory adjustments, and threshold monitoring")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get paginated inventory list", description = "Lấy danh sách tồn kho trang sức kèm phân trang, lọc theo trạng thái (IN_STOCK, LOW_STOCK, OUT_OF_STOCK) và tìm kiếm")
    public ResponseEntity<ApiResponse<PageResponse<InventoryDetailResponse>>> getInventories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<InventoryDetailResponse> response = inventoryService.getInventories(keyword, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{variantId}")
    @Operation(summary = "Get inventory detail by variant ID", description = "Xem chi tiết tồn kho khả dụng, số lượng giữ hàng và lịch sử cập nhật của biến thể")
    public ResponseEntity<ApiResponse<InventoryDetailResponse>> getInventoryByVariantId(
            @PathVariable Long variantId) {
        InventoryDetailResponse response = inventoryService.getInventoryByVariantId(variantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust inventory stock (Restock / Deduct / Audit)", description = "Nhập thêm hàng từ xưởng hoặc kiểm kê điều chỉnh tồn kho an toàn")
    public ResponseEntity<ApiResponse<InventoryDetailResponse>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request) {
        InventoryDetailResponse response = inventoryService.adjustStock(request);
        return ResponseEntity.ok(ApiResponse.success("Điều chỉnh tồn kho thành công!", response));
    }

    @PutMapping("/{variantId}/threshold")
    @Operation(summary = "Update low stock threshold", description = "Cập nhật định mức cảnh báo sắp hết hàng cho biến thể trang sức")
    public ResponseEntity<ApiResponse<InventoryDetailResponse>> updateThreshold(
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateThresholdRequest request) {
        InventoryDetailResponse response = inventoryService.updateThreshold(variantId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật ngưỡng cảnh báo kho thành công!", response));
    }
}
