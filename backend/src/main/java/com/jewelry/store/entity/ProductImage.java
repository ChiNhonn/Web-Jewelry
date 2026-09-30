package com.jewelry.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "product_images",
        indexes = {
                @Index(name = "idx_product_images_product_id", columnList = "product_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "public_id", length = 100)
    private String publicId; // Cloudinary / S3 Key

    @Column(name = "is_thumbnail", nullable = false)
    @Builder.Default
    private boolean isThumbnail = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
