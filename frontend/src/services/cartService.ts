import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';
import { AddToCartRequest, Cart, UpdateCartItemRequest } from '../types/cart.types';

export const cartService = {
  /**
   * Get current user's shopping cart
   */
  async getCart(): Promise<Cart> {
    const response = await apiClient.get<ApiResponse<Cart>>('/api/v1/cart');
    return response.data.data;
  },

  /**
   * Add jewelry variant to cart
   */
  async addToCart(data: AddToCartRequest): Promise<Cart> {
    const response = await apiClient.post<ApiResponse<Cart>>('/api/v1/cart/items', data);
    return response.data.data;
  },

  /**
   * Update quantity of an item in cart
   */
  async updateCartItem(itemId: number, data: UpdateCartItemRequest): Promise<Cart> {
    const response = await apiClient.put<ApiResponse<Cart>>(`/api/v1/cart/items/${itemId}`, data);
    return response.data.data;
  },

  /**
   * Remove item from cart
   */
  async removeCartItem(itemId: number): Promise<Cart> {
    const response = await apiClient.delete<ApiResponse<Cart>>(`/api/v1/cart/items/${itemId}`);
    return response.data.data;
  },

  /**
   * Clear all items from cart
   */
  async clearCart(): Promise<void> {
    await apiClient.delete<ApiResponse<void>>('/api/v1/cart');
  },
};
