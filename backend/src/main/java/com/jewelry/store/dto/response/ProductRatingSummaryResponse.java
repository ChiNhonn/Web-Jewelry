package com.jewelry.store.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRatingSummaryResponse {

    private Long productId;
    private double averageRating;
    private long totalReviews;
    private Map<Integer, Long> ratingBreakdown; // 1 -> count, 2 -> count, ..., 5 -> count
}
