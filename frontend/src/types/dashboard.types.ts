/**
 * Dashboard & Analytics Types for Admin Executive Intelligence
 */

export interface DashboardOverview {
  totalRevenue: number;
  todayRevenue: number;
  monthRevenue: number;
  revenueGrowthPercent: number;
  totalOrders: number;
  pendingOrders: number;
  shippingOrders: number;
  completedOrders: number;
  cancelledOrders: number;
  totalCustomers: number;
  newCustomersThisMonth: number;
  totalProducts: number;
  outOfStockCount: number;
  lowStockCount: number;
}

export interface RevenueChartPoint {
  date: string; // YYYY-MM-DD
  revenue: number;
  orderCount: number;
}

export interface TopSellingProduct {
  productId: number;
  productName: string;
  sku: string;
  unitsSold: number;
  revenue: number;
  thumbnailUrl?: string;
}

export interface CategorySales {
  categoryId: number;
  categoryName: string;
  totalRevenue: number;
  orderCount: number;
  percentage: number;
}

export interface LowStockAlert {
  variantId: number;
  productId: number;
  productName: string;
  sku: string;
  sizeOption?: string;
  materialType?: string;
  quantity: number;
  reservedQuantity: number;
  availableQuantity: number;
  lowStockThreshold: number;
}
