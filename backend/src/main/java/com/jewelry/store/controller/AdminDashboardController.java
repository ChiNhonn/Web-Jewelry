package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.response.*;
import com.jewelry.store.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
@Tag(name = "Admin Analytics & Dashboard", description = "Endpoints for revenue metrics, sales charts, inventory alerts and business intelligence")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @Operation(summary = "Get executive dashboard overview", description = "Thống kê tổng quan doanh thu, đơn hàng, khách hàng và trạng thái kho hàng")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview() {
        DashboardOverviewResponse response = dashboardService.getOverview();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/revenue-analytics")
    @Operation(summary = "Get revenue analytics time-series", description = "Dữ liệu biểu đồ doanh thu và số lượng đơn hàng theo chu kỳ ngày (7, 30 hoặc 90 ngày)")
    public ResponseEntity<ApiResponse<List<RevenueChartDataPoint>>> getRevenueAnalytics(
            @RequestParam(defaultValue = "7") int days) {
        List<RevenueChartDataPoint> response = dashboardService.getRevenueAnalytics(days);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Get top selling jewelry products", description = "Danh sách top sản phẩm bán chạy nhất theo số lượng và doanh thu")
    public ResponseEntity<ApiResponse<List<TopSellingProductResponse>>> getTopProducts(
            @RequestParam(defaultValue = "5") int limit) {
        List<TopSellingProductResponse> response = dashboardService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category-sales")
    @Operation(summary = "Get category sales distribution", description = "Tỷ trọng doanh thu theo từng danh mục trang sức (Nhẫn, Dây chuyền, Vòng tay...)")
    public ResponseEntity<ApiResponse<List<CategorySalesResponse>>> getCategorySales() {
        List<CategorySalesResponse> response = dashboardService.getCategorySales();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/recent-orders")
    @Operation(summary = "Get recent orders", description = "Danh sách đơn hàng mới nhất cần xử lý gấp")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        List<OrderSummaryResponse> response = dashboardService.getRecentOrders(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock inventory alerts", description = "Cảnh báo các biến thể trang sức sắp hết hàng hoặc đã hết hàng")
    public ResponseEntity<ApiResponse<List<LowStockAlertResponse>>> getLowStockAlerts(
            @RequestParam(defaultValue = "10") int limit) {
        List<LowStockAlertResponse> response = dashboardService.getLowStockAlerts(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
