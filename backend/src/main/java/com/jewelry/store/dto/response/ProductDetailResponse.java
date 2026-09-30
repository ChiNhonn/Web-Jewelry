package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String slug;
    private String baseSku;
    private CategoryResponse category;
    private String description;
    private String shortDescription;
    private String materialOverview;
    private String brand;
    private boolean isFeatured;
    private ProductStatus status;
    private List<ProductImageResponse> images;
    private List<ProductVariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
