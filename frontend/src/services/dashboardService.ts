import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';
import { OrderSummary } from '../types/order.types';
import {
  CategorySales,
  DashboardOverview,
  LowStockAlert,
  RevenueChartPoint,
  TopSellingProduct,
} from '../types/dashboard.types';

export const dashboardService = {
  /**
   * Get executive dashboard overview (revenue, orders, customers, inventory)
   */
  async getOverview(): Promise<DashboardOverview> {
    const response = await apiClient.get<ApiResponse<DashboardOverview>>(
      '/api/v1/admin/dashboard/overview'
    );
    return response.data.data;
  },

  /**
   * Get revenue time-series analytics (daily trend with continuous dates)
   */
  async getRevenueAnalytics(days: number = 7): Promise<RevenueChartPoint[]> {
    const response = await apiClient.get<ApiResponse<RevenueChartPoint[]>>(
      `/api/v1/admin/dashboard/revenue-analytics?days=${days}`
    );
    return response.data.data;
  },

  /**
   * Get top selling jewelry products
   */
  async getTopProducts(limit: number = 5): Promise<TopSellingProduct[]> {
    const response = await apiClient.get<ApiResponse<TopSellingProduct[]>>(
      `/api/v1/admin/dashboard/top-products?limit=${limit}`
    );
    return response.data.data;
  },

  /**
   * Get revenue breakdown by jewelry category
   */
  async getCategorySales(): Promise<CategorySales[]> {
    const response = await apiClient.get<ApiResponse<CategorySales[]>>(
      '/api/v1/admin/dashboard/category-sales'
    );
    return response.data.data;
  },

  /**
   * Get recent orders needing attention
   */
  async getRecentOrders(limit: number = 10): Promise<OrderSummary[]> {
    const response = await apiClient.get<ApiResponse<OrderSummary[]>>(
      `/api/v1/admin/dashboard/recent-orders?limit=${limit}`
    );
    return response.data.data;
  },

  /**
   * Get low-stock & out-of-stock inventory alerts
   */
  async getLowStockAlerts(limit: number = 10): Promise<LowStockAlert[]> {
    const response = await apiClient.get<ApiResponse<LowStockAlert[]>>(
      `/api/v1/admin/dashboard/low-stock?limit=${limit}`
    );
    return response.data.data;
  },
};
