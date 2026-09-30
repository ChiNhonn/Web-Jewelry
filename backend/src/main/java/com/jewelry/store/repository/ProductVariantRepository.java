package com.jewelry.store.repository;

import com.jewelry.store.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("SELECT v FROM ProductVariant v LEFT JOIN FETCH v.inventory WHERE v.product.id = :productId")
    List<ProductVariant> findAllByProductIdWithInventory(@Param("productId") Long productId);
}
