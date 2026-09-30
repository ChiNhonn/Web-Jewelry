package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.ReviewResponse;
import com.jewelry.store.entity.Review;
import com.jewelry.store.entity.ReviewImage;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    default ReviewResponse toReviewResponse(Review review) {
        if (review == null) return null;

        List<String> imageUrls = Collections.emptyList();
        if (review.getImages() != null && !review.getImages().isEmpty()) {
            imageUrls = review.getImages().stream()
                    .map(ReviewImage::getImageUrl)
                    .toList();
        }

        String customerName = "Khách hàng";
        if (review.getUser() != null && review.getUser().getFullName() != null) {
            customerName = review.getUser().getFullName();
        }

        String productName = "";
        Long productId = null;
        if (review.getProduct() != null) {
            productId = review.getProduct().getId();
            productName = review.getProduct().getName();
        }

        Long orderId = null;
        if (review.getOrder() != null) {
            orderId = review.getOrder().getId();
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .productId(productId)
                .productName(productName)
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .customerName(customerName)
                .orderId(orderId)
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .build();
    }
}
