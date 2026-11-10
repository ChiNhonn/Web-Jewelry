package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.StockAdjustmentRequest;
import com.jewelry.store.dto.request.UpdateThresholdRequest;
import com.jewelry.store.dto.response.InventoryDetailResponse;
import com.jewelry.store.entity.Product;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.service.InventoryService;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final int LOW_STOCK = 5;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryDetailResponse> getInventories(String keyword, String status, int page, int size) {
        String needle = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<InventoryDetailResponse> all = productRepository.findAll().stream()
                .filter(product -> needle.isEmpty() || product.getName().toLowerCase(Locale.ROOT).contains(needle))
                .map(this::toResponse)
                .filter(row -> status == null || status.isBlank() || status.equalsIgnoreCase(row.getStockStatus()))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        return PageResponse.<InventoryDetailResponse>builder()
                .content(all.subList(from, to))
                .page(safePage)
                .size(safeSize)
                .totalElements(all.size())
                .totalPages(Math.max((int) Math.ceil(all.size() / (double) safeSize), 1))
                .first(safePage == 0)
                .last(to >= all.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDetailResponse getInventoryByVariantId(Long variantId) {
        return toResponse(productRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm")));
    }

    @Override
    @Transactional
    public InventoryDetailResponse adjustStock(StockAdjustmentRequest request) {
        Product product = productRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        int current = product.getQuantity() == null ? 0 : product.getQuantity();
        int next = switch (request.getAdjustmentType()) {
            case ADD -> current + request.getQuantity();
            case DEDUCT -> Math.max(0, current - request.getQuantity());
            case SET -> request.getQuantity();
        };
        product.setQuantity(next);
        return toResponse(productRepository.save(product));
    }

    @Override
    public InventoryDetailResponse updateThreshold(Long variantId, UpdateThresholdRequest request) {
        return getInventoryByVariantId(variantId);
    }

    private InventoryDetailResponse toResponse(Product product) {
        int quantity = product.getQuantity() == null ? 0 : product.getQuantity();
        String stock = quantity <= 0 ? "OUT_OF_STOCK" : quantity <= LOW_STOCK ? "LOW_STOCK" : "IN_STOCK";
        return InventoryDetailResponse.builder()
                .inventoryId(product.getId())
                .variantId(product.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(StoreText.productSlug(product.getId()))
                .sku("SP-" + product.getId())
                .sizeOption(product.getSize())
                .materialType(product.getMaterial())
                .quantity(quantity)
                .reservedQuantity(0)
                .availableQuantity(quantity)
                .lowStockThreshold(LOW_STOCK)
                .stockStatus(stock)
                .updatedAt(product.getCreatedAt())
                .build();
    }
}
