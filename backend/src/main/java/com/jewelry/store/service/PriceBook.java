package com.jewelry.store.service;

import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.Promotion;
import com.jewelry.store.repository.PromotionRepository;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PriceBook {

    private final PromotionRepository promotionRepository;

    public Quote quote(Product product) {
        BigDecimal original = StoreText.money(product.getPrice());
        BigDecimal best = original;
        String promoName = null;
        LocalDateTime now = LocalDateTime.now();
        for (Promotion promotion : promotionRepository.findByActiveTrue()) {
            if (promotion.getStartDate() != null && promotion.getStartDate().isAfter(now)) {
                continue;
            }
            if (promotion.getEndDate() != null && promotion.getEndDate().isBefore(now)) {
                continue;
            }
            if (!applies(promotion, product)) {
                continue;
            }
            BigDecimal sale = apply(original, promotion);
            if (sale.compareTo(best) < 0) {
                best = sale;
                promoName = promotion.getName();
            }
        }
        return new Quote(original, best, promoName, best.compareTo(original) < 0);
    }

    public Integer categoryPercent(Long categoryId) {
        LocalDateTime now = LocalDateTime.now();
        for (Promotion promotion : promotionRepository.findByActiveTrue()) {
            if (!"CATEGORY".equalsIgnoreCase(promotion.getTargetType())) {
                continue;
            }
            if (categoryId == null || !categoryId.equals(promotion.getTargetId())) {
                continue;
            }
            if (!"PERCENTAGE".equalsIgnoreCase(promotion.getDiscountType())) {
                continue;
            }
            if (promotion.getEndDate() != null && promotion.getEndDate().isBefore(now)) {
                continue;
            }
            return promotion.getDiscountValue().intValue();
        }
        return 0;
    }

    public void saveProductDiscount(Product product, BigDecimal original, BigDecimal sale) {
        String code = "PROMO_P_" + product.getId();
        Promotion promotion = promotionRepository.findByCode(code).orElseGet(() -> Promotion.builder()
                .code(code)
                .name("Giá ưu đãi " + product.getName())
                .discountType("FIXED_AMOUNT")
                .targetType("PRODUCT")
                .targetId(product.getId())
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusYears(2))
                .createdAt(LocalDateTime.now())
                .active(true)
                .build());
        if (sale == null || original == null || sale.compareTo(original) >= 0) {
            promotion.setActive(false);
        } else {
            promotion.setActive(true);
            promotion.setDiscountType("FIXED_AMOUNT");
            promotion.setDiscountValue(original.subtract(sale));
            promotion.setTargetType("PRODUCT");
            promotion.setTargetId(product.getId());
            promotion.setName("Giá ưu đãi " + product.getName());
        }
        promotionRepository.save(promotion);
    }

    public void saveCategoryPercent(Long categoryId, String categoryName, Integer percent) {
        String code = "PROMO_CAT_" + categoryId;
        Promotion promotion = promotionRepository.findByCode(code).orElseGet(() -> Promotion.builder()
                .code(code)
                .targetType("CATEGORY")
                .targetId(categoryId)
                .discountType("PERCENTAGE")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusYears(2))
                .createdAt(LocalDateTime.now())
                .build());
        int value = percent == null ? 0 : percent;
        promotion.setName("Ưu đãi " + categoryName);
        promotion.setDiscountValue(BigDecimal.valueOf(value));
        promotion.setActive(value > 0);
        promotion.setTargetId(categoryId);
        promotionRepository.save(promotion);
    }

    private boolean applies(Promotion promotion, Product product) {
        String type = promotion.getTargetType() == null ? "ALL" : promotion.getTargetType();
        if ("ALL".equalsIgnoreCase(type)) {
            return true;
        }
        if ("CATEGORY".equalsIgnoreCase(type)) {
            return product.getCategory() != null && product.getCategory().getId().equals(promotion.getTargetId());
        }
        if ("PRODUCT".equalsIgnoreCase(type)) {
            return product.getId().equals(promotion.getTargetId());
        }
        return false;
    }

    private BigDecimal apply(BigDecimal price, Promotion promotion) {
        if ("PERCENTAGE".equalsIgnoreCase(promotion.getDiscountType())) {
            BigDecimal keep = BigDecimal.valueOf(100).subtract(promotion.getDiscountValue());
            return price.multiply(keep).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        }
        return price.subtract(promotion.getDiscountValue()).max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP);
    }

    public record Quote(BigDecimal original, BigDecimal sale, String promotionName, boolean onPromotion) {
    }
}
