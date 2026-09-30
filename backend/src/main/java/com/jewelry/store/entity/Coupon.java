package com.jewelry.store.entity;

import com.jewelry.store.entity.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupons",
        indexes = {
                @Index(name = "idx_coupons_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code; // VD: JEWELRY10, DIAMOND2026

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_value", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "max_discount", precision = 15, scale = 2)
    private BigDecimal maxDiscount; // Giới hạn tối đa nếu là discount theo %

    @Column(name = "usage_limit", nullable = false)
    @Builder.Default
    private int usageLimit = 100;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private int usedCount = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    public boolean isValidForUse(BigDecimal orderTotal) {
        LocalDateTime now = LocalDateTime.now();
        if (!isActive) return false;
        if (now.isBefore(startDate) || now.isAfter(endDate)) return false;
        if (usedCount >= usageLimit) return false;
        if (minOrderValue != null && orderTotal.compareTo(minOrderValue) < 0) return false;
        return true;
    }
}
