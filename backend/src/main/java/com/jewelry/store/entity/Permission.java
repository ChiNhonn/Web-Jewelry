package com.jewelry.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name; // VD: PRODUCT_CREATE, ORDER_UPDATE

    @Column(name = "description", length = 255)
    private String description;
}
