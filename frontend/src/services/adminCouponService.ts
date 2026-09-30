import apiClient from '../api/axiosClient';
import { ApiResponse, PageResponse } from '../types/api.types';
import {
  AdminCoupon,
  CreateCouponPayload,
  UpdateCouponPayload,
} from '../types/admin.types';

export const adminCouponService = {
  /**
   * Get paginated coupon list with optional active filter
   */
  async getAllCoupons(
    activeOnly?: boolean,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<AdminCoupon>> {
    const params = new URLSearchParams();
    if (activeOnly !== undefined) params.append('activeOnly', activeOnly.toString());
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<ApiResponse<PageResponse<AdminCoupon>>>(
      `/api/v1/admin/coupons?${params.toString()}`
    );
    return response.data.data;
  },

  /**
   * Get coupon details by ID
   */
  async getCouponById(id: number): Promise<AdminCoupon> {
    const response = await apiClient.get<ApiResponse<AdminCoupon>>(
      `/api/v1/admin/coupons/${id}`
    );
    return response.data.data;
  },

  /**
   * Create promotional coupon
   */
  async createCoupon(payload: CreateCouponPayload): Promise<AdminCoupon> {
    const response = await apiClient.post<ApiResponse<AdminCoupon>>(
      '/api/v1/admin/coupons',
      payload
    );
    return response.data.data;
  },

  /**
   * Update coupon
   */
  async updateCoupon(id: number, payload: UpdateCouponPayload): Promise<AdminCoupon> {
    const response = await apiClient.put<ApiResponse<AdminCoupon>>(
      `/api/v1/admin/coupons/${id}`,
      payload
    );
    return response.data.data;
  },

  /**
   * Deactivate coupon
   */
  async deleteCoupon(id: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`/api/v1/admin/coupons/${id}`);
  },
};
