package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String customerName;
    private Long orderId;
    private int rating;
    private String comment;
    private ReviewStatus status;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
