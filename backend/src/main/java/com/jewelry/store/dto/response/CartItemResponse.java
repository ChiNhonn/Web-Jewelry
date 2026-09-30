package com.jewelry.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long variantId;
    private Long productId;
    private String productName;
    private String productSlug;
    private String sku;
    private String sizeOption;
    private String materialType;
    private BigDecimal unitPrice;
    private BigDecimal originalPrice;
    private int quantity;
    private BigDecimal subtotal;
    private String thumbnailUrl;
    private int availableStock;
    private boolean inStock;
}
