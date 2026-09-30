package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.ProductStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {

    private String keyword;
    private Long categoryId;
    private String categorySlug;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String material;
    private String brand;
    private Boolean isFeatured;

    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Min(value = 0, message = "Số trang không được nhỏ hơn 0")
    @Builder.Default
    private int page = 0;

    @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
    @Max(value = 100, message = "Kích thước trang tối đa là 100")
    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String sortBy = "createdAt"; // createdAt, name, price

    @Builder.Default
    private String sortDirection = "desc"; // asc, desc
}
