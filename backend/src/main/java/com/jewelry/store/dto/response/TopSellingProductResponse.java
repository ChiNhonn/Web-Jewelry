package com.jewelry.store.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSellingProductResponse {

    private Long productId;
    private String productName;
    private String sku;
    private long unitsSold;
    private BigDecimal revenue;
    private String thumbnailUrl;
}
