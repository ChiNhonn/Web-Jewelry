import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';
import { WishlistItem } from '../types/cart.types';

export const wishlistService = {
  /**
   * Get user's wishlist
   */
  async getWishlist(): Promise<WishlistItem[]> {
    const response = await apiClient.get<ApiResponse<WishlistItem[]>>('/api/v1/wishlist');
    return response.data.data;
  },

  /**
   * Add a product to wishlist
   */
  async addToWishlist(productId: number): Promise<WishlistItem> {
    const response = await apiClient.post<ApiResponse<WishlistItem>>(`/api/v1/wishlist/${productId}`);
    return response.data.data;
  },

  /**
   * Remove a product from wishlist
   */
  async removeFromWishlist(productId: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`/api/v1/wishlist/${productId}`);
  },
};
