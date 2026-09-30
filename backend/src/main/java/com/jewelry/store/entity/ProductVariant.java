package com.jewelry.store.entity;

import com.jewelry.store.entity.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "product_variants",
        indexes = {
                @Index(name = "idx_product_variants_sku", columnList = "sku"),
                @Index(name = "idx_product_variants_product_id", columnList = "product_id"),
                @Index(name = "idx_product_variants_price", columnList = "sale_price")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", unique = true, nullable = false, length = 60)
    private String sku; // VD: RG-SOLITAIRE-18K-SZ16

    @Column(name = "size_option", length = 50)
    private String sizeOption; // Size 6, 7, 8, hoặc 45cm

    @Column(name = "material_type", length = 100)
    private String materialType; // Vàng Trắng 18K, Vàng Hồng 18K, Platinum

    @Column(name = "original_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "sale_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory inventory;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        if (inventory != null) {
            inventory.setVariant(this);
        }
    }
}
