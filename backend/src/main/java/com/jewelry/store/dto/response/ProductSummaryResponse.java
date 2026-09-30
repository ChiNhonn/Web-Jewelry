package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponse {

    private Long id;
    private String name;
    private String slug;
    private String baseSku;
    private String categoryName;
    private String categorySlug;
    private String brand;
    private String materialOverview;
    private boolean isFeatured;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String thumbnailUrl;
    private ProductStatus status;
    private LocalDateTime createdAt;
}
