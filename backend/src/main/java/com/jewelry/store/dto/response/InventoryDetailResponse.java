package com.jewelry.store.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDetailResponse {

    private Long inventoryId;
    private Long variantId;
    private Long productId;
    private String productName;
    private String productSlug;
    private String sku;
    private String sizeOption;
    private String materialType;
    private int quantity;
    private int reservedQuantity;
    private int availableQuantity;
    private int lowStockThreshold;
    private Long version;
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    private LocalDateTime updatedAt;
}
