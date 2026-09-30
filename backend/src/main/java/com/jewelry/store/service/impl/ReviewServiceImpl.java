package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateReviewRequest;
import com.jewelry.store.dto.request.UpdateReviewStatusRequest;
import com.jewelry.store.dto.response.ProductRatingSummaryResponse;
import com.jewelry.store.dto.response.ReviewResponse;
import com.jewelry.store.entity.*;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.ReviewStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.mapper.ReviewMapper;
import com.jewelry.store.repository.*;
import com.jewelry.store.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request) {
        log.info("Creating review for user ID: {}, product ID: {}, order ID: {}", userId, productId, request.getOrderId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + request.getOrderId()));

        // 1. Kiểm tra quyền sở hữu đơn hàng
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Bạn không thể đánh giá sản phẩm bằng đơn hàng của người khác");
        }

        // 2. Kiểm tra Verified Purchase: Đơn hàng phải đã giao thành công
        if (order.getOrderStatus() != OrderStatus.DELIVERED && order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new BadRequestException("Chỉ khách hàng đã nhận trang sức thành công mới có thể gửi đánh giá cho sản phẩm này");
        }

        // 3. Kiểm tra sản phẩm có thực sự nằm trong đơn hàng đã mua hay không
        boolean productPurchasedInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getVariant() != null &&
                        item.getVariant().getProduct() != null &&
                        item.getVariant().getProduct().getId().equals(productId));

        if (!productPurchasedInOrder) {
            throw new BadRequestException("Đơn hàng này không chứa sản phẩm trang sức bạn đang muốn đánh giá");
        }

        // 4. Ngăn chặn đánh giá trùng lặp cho cùng một đơn hàng
        if (reviewRepository.existsByUserIdAndProductIdAndOrderId(userId, productId, order.getId())) {
            throw new BadRequestException("Bạn đã gửi đánh giá cho sản phẩm này trong đơn hàng đã chọn rồi");
        }

        // 5. Tạo Review thực thể
        Review review = Review.builder()
                .product(product)
                .user(user)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .status(ReviewStatus.PENDING)
                .build();

        // 6. Đính kèm danh sách ảnh feedback thực tế nếu có
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    ReviewImage image = ReviewImage.builder()
                            .imageUrl(imageUrl.trim())
                            .build();
                    review.addImage(image);
                }
            }
        }

        Review saved = reviewRepository.save(review);
        log.info("Review created ID: {} (Status: PENDING) for product ID: {}", saved.getId(), productId);
        return reviewMapper.toReviewResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable);
        return PageResponse.from(reviewPage.map(reviewMapper::toReviewResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductRatingSummaryResponse getProductRatingSummary(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Sản phẩm không tồn tại");
        }

        Double avgRating = reviewRepository.getAverageRatingByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        Long totalReviews = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED);

        double roundedAvg = 0.0;
        if (avgRating != null) {
            roundedAvg = BigDecimal.valueOf(avgRating)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        Map<Integer, Long> breakdown = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            breakdown.put(i, 0L);
        }

        List<Object[]> rawBreakdown = reviewRepository.getRatingBreakdownByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        if (rawBreakdown != null) {
            for (Object[] row : rawBreakdown) {
                Integer star = (Integer) row[0];
                Long count = (Long) row[1];
                if (star != null) {
                    breakdown.put(star, count != null ? count : 0L);
                }
            }
        }

        return ProductRatingSummaryResponse.builder()
                .productId(productId)
                .averageRating(roundedAvg)
                .totalReviews(totalReviews != null ? totalReviews : 0L)
                .ratingBreakdown(breakdown)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getAllReviewsForAdmin(ReviewStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage;
        if (status != null) {
            reviewPage = reviewRepository.findByStatus(status, pageable);
        } else {
            reviewPage = reviewRepository.findAll(pageable);
        }
        return PageResponse.from(reviewPage.map(reviewMapper::toReviewResponse));
    }

    @Override
    @Transactional
    public ReviewResponse updateReviewStatusByAdmin(Long reviewId, UpdateReviewStatusRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá: " + reviewId));

        review.setStatus(request.getStatus());
        Review updated = reviewRepository.save(review);
        log.info("Admin updated review ID: {} status to: {}", reviewId, request.getStatus());
        return reviewMapper.toReviewResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá: " + reviewId));
        reviewRepository.delete(review);
        log.info("Admin deleted review ID: {}", reviewId);
    }
}
