package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CreateReviewRequest;
import com.jewelry.store.dto.request.UpdateReviewStatusRequest;
import com.jewelry.store.dto.response.ProductRatingSummaryResponse;
import com.jewelry.store.dto.response.ReviewResponse;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.OrderDetail;
import com.jewelry.store.entity.ProductReview;
import com.jewelry.store.entity.User;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.ReviewStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.OrderDetailRepository;
import com.jewelry.store.repository.OrderRepository;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.repository.ProductReviewRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn hàng"));
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Đơn hàng không thuộc tài khoản này");
        }
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Chỉ đánh giá sau khi đơn đã giao");
        }
        boolean bought = orderDetailRepository.findByOrderId(order.getId()).stream()
                .anyMatch(detail -> productId.equals(detail.getProductId()));
        if (!bought) {
            throw new BadRequestException("Đơn hàng không có sản phẩm này");
        }
        ProductReview review = reviewRepository.save(ProductReview.builder()
                .productId(productId)
                .orderId(order.getId())
                .user(user)
                .userName(user.getFullName())
                .rating(request.getRating())
                .comment(request.getComment())
                .visible(true)
                .createdAt(LocalDateTime.now())
                .build());
        return toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        List<ReviewResponse> all = reviewRepository.findByProductIdAndVisibleTrueOrderByCreatedAtDesc(productId).stream()
                .map(this::toResponse)
                .toList();
        return page(all, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getLatestVisibleReviews(int limit) {
        int safeLimit = limit <= 0 ? 6 : limit;
        return reviewRepository.findAll().stream()
                .filter(ProductReview::isVisible)
                .sorted(Comparator
                        .comparing((ProductReview review) -> review.getComment() == null || review.getComment().isBlank())
                        .thenComparing(ProductReview::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(safeLimit)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductRatingSummaryResponse getProductRatingSummary(Long productId) {
        List<ProductReview> reviews = reviewRepository.findByProductIdAndVisibleTrueOrderByCreatedAtDesc(productId);
        Map<Integer, Long> breakdown = new HashMap<>();
        for (int star = 1; star <= 5; star++) {
            breakdown.put(star, 0L);
        }
        reviews.forEach(review -> breakdown.merge(review.getRating(), 1L, Long::sum));
        double average = reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0);
        return ProductRatingSummaryResponse.builder()
                .productId(productId)
                .averageRating(average)
                .totalReviews(reviews.size())
                .ratingBreakdown(breakdown)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getAllReviewsForAdmin(ReviewStatus status, int page, int size) {
        List<ReviewResponse> all = reviewRepository.findAll().stream()
                .filter(review -> status == null || statusOf(review) == status)
                .map(this::toResponse)
                .toList();
        return page(all, page, size);
    }

    @Override
    @Transactional
    public ReviewResponse updateReviewStatusByAdmin(Long reviewId, UpdateReviewStatusRequest request) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        review.setVisible(request.getStatus() != ReviewStatus.REJECTED);
        return toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponse toResponse(ProductReview review) {
        String productName = productRepository.findById(review.getProductId()).map(product -> product.getName()).orElse("");
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .productName(productName)
                .userId(review.getUser().getId())
                .customerName(review.getUserName())
                .orderId(review.getOrderId())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(statusOf(review))
                .imageUrls(List.of())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private ReviewStatus statusOf(ProductReview review) {
        return review.isVisible() ? ReviewStatus.APPROVED : ReviewStatus.REJECTED;
    }

    private <T> PageResponse<T> page(List<T> all, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : size;
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        int pages = (int) Math.ceil(all.size() / (double) safeSize);
        return PageResponse.<T>builder()
                .content(all.subList(from, to))
                .page(safePage)
                .size(safeSize)
                .totalElements(all.size())
                .totalPages(Math.max(pages, 1))
                .first(safePage == 0)
                .last(safePage >= pages - 1)
                .build();
    }
}
