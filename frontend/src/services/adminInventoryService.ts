import apiClient from '../api/axiosClient';
import { ApiResponse, PageResponse } from '../types/api.types';
import {
  InventoryItem,
  InventoryStatus,
  StockAdjustmentPayload,
  UpdateThresholdPayload,
} from '../types/admin.types';

export const adminInventoryService = {
  /**
   * Get paginated inventory list with optional filters
   */
  async getInventories(
    keyword?: string,
    status?: InventoryStatus,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<InventoryItem>> {
    const params = new URLSearchParams();
    if (keyword) params.append('keyword', keyword);
    if (status) params.append('status', status);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<ApiResponse<PageResponse<InventoryItem>>>(
      `/api/v1/admin/inventory?${params.toString()}`
    );
    return response.data.data;
  },

  /**
   * Get inventory detail by variant ID
   */
  async getInventoryByVariantId(variantId: number): Promise<InventoryItem> {
    const response = await apiClient.get<ApiResponse<InventoryItem>>(
      `/api/v1/admin/inventory/${variantId}`
    );
    return response.data.data;
  },

  /**
   * Adjust stock (ADD/Restock, DEDUCT/Waste, SET/Audit)
   */
  async adjustStock(payload: StockAdjustmentPayload): Promise<InventoryItem> {
    const response = await apiClient.post<ApiResponse<InventoryItem>>(
      '/api/v1/admin/inventory/adjust',
      payload
    );
    return response.data.data;
  },

  /**
   * Update low stock threshold for a variant
   */
  async updateThreshold(
    variantId: number,
    payload: UpdateThresholdPayload
  ): Promise<InventoryItem> {
    const response = await apiClient.put<ApiResponse<InventoryItem>>(
      `/api/v1/admin/inventory/${variantId}/threshold`,
      payload
    );
    return response.data.data;
  },
};
