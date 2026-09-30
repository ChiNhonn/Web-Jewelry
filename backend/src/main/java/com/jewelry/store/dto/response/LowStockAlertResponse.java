package com.jewelry.store.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockAlertResponse {

    private Long variantId;
    private Long productId;
    private String productName;
    private String sku;
    private String sizeOption;
    private String materialType;
    private int quantity;
    private int reservedQuantity;
    private int availableQuantity;
    private int lowStockThreshold;
}
