package com.jewelry.store.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardOverviewResponse {

    // Tài chính doanh thu
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private double revenueGrowthPercent; // % tăng/giảm so với tháng trước

    // Chỉ số đơn hàng
    private long totalOrders;
    private long pendingOrders;
    private long shippingOrders;
    private long completedOrders;
    private long cancelledOrders;

    // Khách hàng & Kho hàng
    private long totalCustomers;
    private long newCustomersThisMonth;
    private long totalProducts;
    private long outOfStockCount;
    private long lowStockCount;
}
