package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateReviewRequest;
import com.jewelry.store.dto.response.ProductRatingSummaryResponse;
import com.jewelry.store.dto.response.ReviewResponse;
import com.jewelry.store.security.UserPrincipal;
import com.jewelry.store.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Customer Reviews & Ratings", description = "Endpoints for customer product reviews and star rating metrics")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get product reviews", description = "Lấy danh sách các nhận xét đánh giá đã được kiểm duyệt duyệt (APPROVED) của sản phẩm")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ReviewResponse> response = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get product rating summary", description = "Lấy điểm đánh giá trung bình và thống kê số lượng theo từng mức 1 - 5 sao")
    public ResponseEntity<ApiResponse<ProductRatingSummaryResponse>> getRatingSummary(
            @PathVariable Long productId) {
        ProductRatingSummaryResponse response = reviewService.getProductRatingSummary(productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create review for product", description = "Khách hàng gửi đánh giá kèm hình ảnh thực tế (Yêu cầu Verified Purchase)")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createReview(currentUser.getId(), productId, request);
        return new ResponseEntity<>(ApiResponse.success("Đã gửi nhận xét thành công! Đánh giá sẽ hiển thị sau khi được kiểm duyệt.", response), HttpStatus.CREATED);
    }
}
