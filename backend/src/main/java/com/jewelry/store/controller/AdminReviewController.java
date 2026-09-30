package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.UpdateReviewStatusRequest;
import com.jewelry.store.dto.response.ReviewResponse;
import com.jewelry.store.entity.enums.ReviewStatus;
import com.jewelry.store.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
@Tag(name = "Admin Review Moderation", description = "Endpoints for moderating customer reviews and managing feedback")
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "List all reviews for moderation", description = "Lấy danh sách tất cả các đánh giá kèm phân trang và lọc theo trạng thái (PENDING, APPROVED, REJECTED)")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getAllReviews(
            @RequestParam(required = false) ReviewStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ReviewResponse> response = reviewService.getAllReviewsForAdmin(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{reviewId}/status")
    @Operation(summary = "Moderate review status", description = "Phê duyệt (APPROVED) hoặc từ chối (REJECTED) hiển thị đánh giá của khách")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReviewStatus(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewStatusRequest request) {
        ReviewResponse response = reviewService.updateReviewStatusByAdmin(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái kiểm duyệt thành công!", response));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete review", description = "Xóa hoàn toàn đánh giá vi phạm tiêu chuẩn cộng đồng")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.ok(ApiResponse.empty("Đã xóa đánh giá thành công!"));
    }
}
