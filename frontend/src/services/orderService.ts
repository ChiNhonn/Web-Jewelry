import apiClient from '../api/axiosClient';
import { ApiResponse, PageResponse } from '../types/api.types';
import {
  CouponValidation,
  CreateOrderRequest,
  Order,
  OrderStatus,
  OrderSummary,
} from '../types/order.types';

export const orderService = {
  /**
   * Checkout & place new order
   */
  async checkout(data: CreateOrderRequest): Promise<Order> {
    const response = await apiClient.post<ApiResponse<Order>>('/api/v1/orders', data);
    return response.data.data;
  },

  /**
   * Get customer order history with pagination & optional status filter
   */
  async getUserOrders(
    status?: OrderStatus,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<OrderSummary>> {
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<ApiResponse<PageResponse<OrderSummary>>>(
      `/api/v1/orders?${params.toString()}`
    );
    return response.data.data;
  },

  /**
   * Get detailed order info by order code
   */
  async getOrderDetails(orderCode: string): Promise<Order> {
    const response = await apiClient.get<ApiResponse<Order>>(`/api/v1/orders/${orderCode}`);
    return response.data.data;
  },

  /**
   * Customer cancel order
   */
  async cancelOrder(orderCode: string, cancellationReason?: string): Promise<Order> {
    const response = await apiClient.put<ApiResponse<Order>>(
      `/api/v1/orders/${orderCode}/cancel`,
      { cancellationReason }
    );
    return response.data.data;
  },

  /**
   * Validate and preview coupon discount
   */
  async validateCoupon(code: string, amount: number): Promise<CouponValidation> {
    const response = await apiClient.get<ApiResponse<CouponValidation>>(
      `/api/v1/coupons/validate?code=${encodeURIComponent(code)}&amount=${amount}`
    );
    return response.data.data;
  },

  /**
   * Admin: Get all store orders
   */
  async getAllOrders(
    status?: OrderStatus,
    keyword?: string,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<OrderSummary>> {
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (keyword) params.append('keyword', keyword);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<ApiResponse<PageResponse<OrderSummary>>>(
      `/api/v1/admin/orders?${params.toString()}`
    );
    return response.data.data;
  },

  /**
   * Admin: Get order details
   */
  async getAdminOrderDetails(orderCode: string): Promise<Order> {
    const response = await apiClient.get<ApiResponse<Order>>(`/api/v1/admin/orders/${orderCode}`);
    return response.data.data;
  },

  /**
   * Admin: Update order status
   */
  async updateOrderStatus(
    orderCode: string,
    newStatus: OrderStatus,
    note?: string
  ): Promise<Order> {
    const response = await apiClient.put<ApiResponse<Order>>(
      `/api/v1/admin/orders/${orderCode}/status`,
      { newStatus, note }
    );
    return response.data.data;
  },

  /**
   * Admin: Cancel order
   */
  async cancelOrderByAdmin(orderCode: string, cancellationReason?: string): Promise<Order> {
    const response = await apiClient.put<ApiResponse<Order>>(
      `/api/v1/admin/orders/${orderCode}/cancel`,
      { cancellationReason }
    );
    return response.data.data;
  },
};
