package com.jewelry.store.entity;

import com.jewelry.store.entity.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_products_slug", columnList = "slug"),
                @Index(name = "idx_products_base_sku", columnList = "base_sku"),
                @Index(name = "idx_products_category_id", columnList = "category_id"),
                @Index(name = "idx_products_status", columnList = "status"),
                @Index(name = "idx_products_featured", columnList = "is_featured")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 250)
    private String slug;

    @Column(name = "base_sku", unique = true, nullable = false, length = 50)
    private String baseSku;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "material_overview", length = 150)
    private String materialOverview;

    @Column(name = "weight_grams", precision = 8, scale = 2)
    private BigDecimal weightGrams;

    @Column(name = "stone_type", length = 100)
    private String stoneType;

    @Column(name = "clarity", length = 80)
    private String clarity;

    @Column(name = "warranty", length = 200)
    private String warranty;

    @Column(name = "brand", length = 100)
    @Builder.Default
    private String brand = "AURA JEWELRY";

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }
}
