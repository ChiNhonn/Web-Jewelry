import apiClient from '../api/axiosClient';
import { ApiResponse, PageResponse } from '../types/api.types';
import { ProductDetail, ProductSummary } from '../types/product.types';
import {
  AddProductImagePayload,
  CreateVariantPayload,
  UpdateVariantPayload,
} from '../types/admin.types';

export const adminProductService = {
  /**
   * List all products for admin table with pagination and keyword search
   */
  async getAllProducts(
    keyword?: string,
    categoryId?: number,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<ProductSummary>> {
    const params = new URLSearchParams();
    if (keyword) params.append('keyword', keyword);
    if (categoryId) params.append('categoryId', categoryId.toString());
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<ApiResponse<PageResponse<ProductSummary>>>(
      `/api/v1/admin/products?${params.toString()}`
    );
    return response.data.data;
  },

  /**
   * Get product detail by ID (Admin view)
   */
  async getProductById(id: number): Promise<ProductDetail> {
    const response = await apiClient.get<ApiResponse<ProductDetail>>(
      `/api/v1/admin/products/${id}`
    );
    return response.data.data;
  },

  /**
   * Add a new variant to an existing jewelry product
   */
  async addVariant(
    productId: number,
    payload: CreateVariantPayload
  ): Promise<ProductDetail> {
    const response = await apiClient.post<ApiResponse<ProductDetail>>(
      `/api/v1/admin/products/${productId}/variants`,
      payload
    );
    return response.data.data;
  },

  /**
   * Update existing variant
   */
  async updateVariant(
    productId: number,
    variantId: number,
    payload: UpdateVariantPayload
  ): Promise<ProductDetail> {
    const response = await apiClient.put<ApiResponse<ProductDetail>>(
      `/api/v1/admin/products/${productId}/variants/${variantId}`,
      payload
    );
    return response.data.data;
  },

  /**
   * Deactivate a variant
   */
  async deleteVariant(productId: number, variantId: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(
      `/api/v1/admin/products/${productId}/variants/${variantId}`
    );
  },

  /**
   * Add product image
   */
  async addImage(
    productId: number,
    payload: AddProductImagePayload
  ): Promise<ProductDetail> {
    const response = await apiClient.post<ApiResponse<ProductDetail>>(
      `/api/v1/admin/products/${productId}/images`,
      payload
    );
    return response.data.data;
  },

  /**
   * Delete product image
   */
  async deleteImage(productId: number, imageId: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(
      `/api/v1/admin/products/${productId}/images/${imageId}`
    );
  },
};
