package com.jewelry.store.service;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.StockAdjustmentRequest;
import com.jewelry.store.dto.request.UpdateThresholdRequest;
import com.jewelry.store.dto.response.InventoryDetailResponse;

public interface InventoryService {

    PageResponse<InventoryDetailResponse> getInventories(String keyword, String status, int page, int size);

    InventoryDetailResponse getInventoryByVariantId(Long variantId);

    InventoryDetailResponse adjustStock(StockAdjustmentRequest request);

    InventoryDetailResponse updateThreshold(Long variantId, UpdateThresholdRequest request);
}
