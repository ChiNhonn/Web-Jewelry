import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';
import { Category, ProductDetail } from '../types/product.types';

export const productService = {
  /**
   * Get Category navigation hierarchy
   */
  async getCategories(): Promise<Category[]> {
    const response = await apiClient.get<ApiResponse<Category[]>>('/api/v1/categories');
    return response.data.data;
  },

  /**
   * Get category detail by slug
   */
  async getCategoryBySlug(slug: string): Promise<Category> {
    const response = await apiClient.get<ApiResponse<Category>>(`/api/v1/categories/${slug}`);
    return response.data.data;
  },

  /**
   * Get full product detail by slug
   */
  async getProductBySlug(slug: string): Promise<ProductDetail> {
    const response = await apiClient.get<ApiResponse<ProductDetail>>(`/api/v1/products/${slug}`);
    return response.data.data;
  },

  /**
   * Search and filter products with pagination
   */
  async filterProducts(params?: import('../types/product.types').ProductFilterParams): Promise<import('../types/api.types').PageResponse<import('../types/product.types').ProductSummary>> {
    const response = await apiClient.get<ApiResponse<import('../types/api.types').PageResponse<import('../types/product.types').ProductSummary>>>('/api/v1/products', {
      params,
    });
    return response.data.data;
  },
};
