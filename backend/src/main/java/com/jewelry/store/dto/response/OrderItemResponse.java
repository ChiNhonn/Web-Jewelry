package com.jewelry.store.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long variantId;
    private Long productId;
    private String productName;
    private String productSlug;
    private String sku;
    private String variantAttributes;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;
    private String thumbnailUrl;
}
