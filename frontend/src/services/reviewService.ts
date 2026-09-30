import apiClient from '../api/axiosClient';
import { ApiResponse, PageResponse } from '../types/api.types';
import {
  CreateReviewRequest,
  ProductRatingSummary,
  Review,
  ReviewStatus,
  UpdateReviewStatusRequest,
} from '../types/review.types';

export const reviewService = {
  /**
   * Get approved reviews for a product with pagination
   */
  async getProductReviews(
    productId: number,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<Review>> {
    const response = await apiClient.get<ApiResponse<PageResponse<Review>>>(
      `/api/v1/products/${productId}/reviews?page=${page}&size=${size}`
    );
    return response.data.data;
  },

  /**
   * Get rating summary and star breakdown for a product
   */
  async getProductRatingSummary(productId: number): Promise<ProductRatingSummary> {
    const response = await apiClient.get<ApiResponse<ProductRatingSummary>>(
      `/api/v1/products/${productId}/reviews/summary`
    );
    return response.data.data;
  },

  /**
   * Customer submits a review (Requires Verified Purchase)
   */
  async createReview(productId: number, data: CreateReviewRequest): Promise<Review> {
    const response = await apiClient.post<ApiResponse<Review>>(
      `/api/v1/products/${productId}/reviews`,
      data
    );
    return response.data.data;
  },

  /**
   * Admin: Get all reviews for moderation
   */
  async getAllReviewsForAdmin(
    status?: ReviewStatus,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<Review>> {
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<ApiResponse<PageResponse<Review>>>(
      `/api/v1/admin/reviews?${params.toString()}`
    );
    return response.data.data;
  },

  /**
   * Admin: Approve or Reject a review
   */
  async updateReviewStatus(
    reviewId: number,
    data: UpdateReviewStatusRequest
  ): Promise<Review> {
    const response = await apiClient.put<ApiResponse<Review>>(
      `/api/v1/admin/reviews/${reviewId}/status`,
      data
    );
    return response.data.data;
  },

  /**
   * Admin: Delete an abusive review
   */
  async deleteReview(reviewId: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`/api/v1/admin/reviews/${reviewId}`);
  },
};
