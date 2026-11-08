package com.jewelry.store.util;

import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.ProductVariant;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PricingUtils {

    private PricingUtils() {
    }

    public static BigDecimal effectiveSale(ProductVariant variant) {
        if (variant == null || variant.getSalePrice() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal sale = variant.getSalePrice();
        BigDecimal original = variant.getOriginalPrice();
        Integer percent = categoryPercent(variant.getProduct());
        if (percent != null && percent > 0 && percent < 100
                && original != null
                && sale.compareTo(original) >= 0) {
            return original.multiply(BigDecimal.valueOf(100 - percent))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        }
        return sale;
    }

    public static boolean onPromotion(ProductVariant variant) {
        if (variant == null) {
            return false;
        }
        Integer percent = categoryPercent(variant.getProduct());
        if (percent != null && percent > 0) {
            return true;
        }
        return variant.getOriginalPrice() != null
                && variant.getSalePrice() != null
                && variant.getSalePrice().compareTo(variant.getOriginalPrice()) < 0;
    }

    private static Integer categoryPercent(Product product) {
        if (product == null || product.getCategory() == null) {
            return null;
        }
        Category category = product.getCategory();
        return category.getPromotionPercent();
    }
}
