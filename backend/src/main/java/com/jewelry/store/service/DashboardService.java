package com.jewelry.store.service;

import com.jewelry.store.dto.response.*;

import java.util.List;

public interface DashboardService {

    DashboardOverviewResponse getOverview();

    List<RevenueChartDataPoint> getRevenueAnalytics(int days);

    List<TopSellingProductResponse> getTopSellingProducts(int limit);

    List<CategorySalesResponse> getCategorySales();

    List<OrderSummaryResponse> getRecentOrders(int limit);

    List<LowStockAlertResponse> getLowStockAlerts(int limit);
}
