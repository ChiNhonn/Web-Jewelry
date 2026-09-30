package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.StockAdjustmentRequest;
import com.jewelry.store.dto.request.UpdateThresholdRequest;
import com.jewelry.store.dto.response.InventoryDetailResponse;
import com.jewelry.store.entity.Inventory;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.ProductVariant;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.InventoryRepository;
import com.jewelry.store.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryDetailResponse> getInventories(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String normalizedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String normalizedStatus = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : null;

        Page<Inventory> inventoryPage = inventoryRepository.findAllInventories(normalizedKeyword, normalizedStatus, pageable);
        Page<InventoryDetailResponse> responsePage = inventoryPage.map(this::mapToDetailResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDetailResponse getInventoryByVariantId(Long variantId) {
        Inventory inventory = inventoryRepository.findByVariantIdWithDetails(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho cho biến thể ID: " + variantId));
        return mapToDetailResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryDetailResponse adjustStock(StockAdjustmentRequest request) {
        Inventory inventory = inventoryRepository.findByVariantIdWithDetails(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho cho biến thể ID: " + request.getVariantId()));

        int oldQuantity = inventory.getQuantity();
        switch (request.getAdjustmentType()) {
            case ADD -> inventory.setQuantity(oldQuantity + request.getQuantity());
            case DEDUCT -> {
                if (inventory.getAvailableQuantity() < request.getQuantity()) {
                    throw new BadRequestException("Số lượng xuất hủy (" + request.getQuantity()
                            + ") lớn hơn số lượng khả dụng hiện có (" + inventory.getAvailableQuantity() + ")");
                }
                inventory.setQuantity(oldQuantity - request.getQuantity());
            }
            case SET -> {
                if (request.getQuantity() < inventory.getReservedQuantity()) {
                    throw new BadRequestException("Số lượng tồn kho mới (" + request.getQuantity()
                            + ") không được nhỏ hơn số lượng đã được đặt giữ trước (" + inventory.getReservedQuantity() + ")");
                }
                inventory.setQuantity(request.getQuantity());
            }
        }

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory adjusted for variantId: {}. Type: {}, Old: {}, New: {}, Reason: '{}'",
                request.getVariantId(), request.getAdjustmentType(), oldQuantity, saved.getQuantity(), request.getReason());

        return mapToDetailResponse(saved);
    }

    @Override
    @Transactional
    public InventoryDetailResponse updateThreshold(Long variantId, UpdateThresholdRequest request) {
        Inventory inventory = inventoryRepository.findByVariantIdWithDetails(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tồn kho cho biến thể ID: " + variantId));

        inventory.setLowStockThreshold(request.getLowStockThreshold());
        Inventory saved = inventoryRepository.save(inventory);
        log.info("Low stock threshold updated for variantId: {} to {}", variantId, request.getLowStockThreshold());

        return mapToDetailResponse(saved);
    }

    private InventoryDetailResponse mapToDetailResponse(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        Product product = variant.getProduct();
        int avail = inventory.getAvailableQuantity();

        String status;
        if (avail <= 0) {
            status = "OUT_OF_STOCK";
        } else if (avail <= inventory.getLowStockThreshold()) {
            status = "LOW_STOCK";
        } else {
            status = "IN_STOCK";
        }

        return InventoryDetailResponse.builder()
                .inventoryId(inventory.getId())
                .variantId(variant.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .sku(variant.getSku())
                .sizeOption(variant.getSizeOption())
                .materialType(variant.getMaterialType())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(avail)
                .lowStockThreshold(inventory.getLowStockThreshold())
                .version(inventory.getVersion())
                .stockStatus(status)
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
