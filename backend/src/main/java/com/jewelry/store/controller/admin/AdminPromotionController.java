package com.jewelry.store.controller.admin;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.response.PromotionAdminResponse;
import com.jewelry.store.entity.Promotion;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromotionController {

    private final PromotionRepository promotionRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionAdminResponse>>> list() {
        List<PromotionAdminResponse> promotions = promotionRepository.findAll().stream()
                .sorted(Comparator.comparing(Promotion::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<ApiResponse<PromotionAdminResponse>> setActive(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi"));
        if (body.get("active") != null) {
            promotion.setActive(body.get("active"));
        }
        return ResponseEntity.ok(ApiResponse.success(toResponse(promotionRepository.save(promotion))));
    }

    private PromotionAdminResponse toResponse(Promotion promotion) {
        return PromotionAdminResponse.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .targetType(promotion.getTargetType())
                .targetId(promotion.getTargetId())
                .active(promotion.isActive())
                .build();
    }
}
