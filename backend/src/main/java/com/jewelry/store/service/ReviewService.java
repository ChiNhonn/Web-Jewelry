package com.jewelry.store.service;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateReviewRequest;
import com.jewelry.store.dto.request.UpdateReviewStatusRequest;
import com.jewelry.store.dto.response.ProductRatingSummaryResponse;
import com.jewelry.store.dto.response.ReviewResponse;
import com.jewelry.store.entity.enums.ReviewStatus;

public interface ReviewService {

    ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request);

    PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size);

    ProductRatingSummaryResponse getProductRatingSummary(Long productId);

    PageResponse<ReviewResponse> getAllReviewsForAdmin(ReviewStatus status, int page, int size);

    ReviewResponse updateReviewStatusByAdmin(Long reviewId, UpdateReviewStatusRequest request);

    void deleteReviewByAdmin(Long reviewId);
}
