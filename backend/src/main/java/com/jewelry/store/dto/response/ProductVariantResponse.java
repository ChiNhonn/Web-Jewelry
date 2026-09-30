package com.jewelry.store.dto.response;

import com.jewelry.store.entity.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {

    private Long id;
    private String sku;
    private String sizeOption;
    private String materialType;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private ProductStatus status;
    private int stockQuantity;
    private int availableQuantity;
}
