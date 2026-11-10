package com.jewelry.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String material;

    @Column(length = 100)
    private String weight;

    @Column(name = "image_name", length = 500)
    private String imageName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "gem_type")
    private String gemType;

    private String purity;

    @Column(length = 100)
    private String size;

    private String warranty;
}
