package com.jewelry.store.repository;

import com.jewelry.store.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByVariantId(Long variantId);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.variant v JOIN FETCH v.product p WHERE v.id = :variantId")
    Optional<Inventory> findByVariantIdWithDetails(@org.springframework.data.repository.query.Param("variantId") Long variantId);

    @Query(value = "SELECT i FROM Inventory i JOIN FETCH i.variant v JOIN FETCH v.product p " +
            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR " +
            "     (:status = 'OUT_OF_STOCK' AND (i.quantity - i.reservedQuantity) <= 0) OR " +
            "     (:status = 'LOW_STOCK' AND (i.quantity - i.reservedQuantity) > 0 AND (i.quantity - i.reservedQuantity) <= i.lowStockThreshold) OR " +
            "     (:status = 'IN_STOCK' AND (i.quantity - i.reservedQuantity) > i.lowStockThreshold))",
            countQuery = "SELECT COUNT(i) FROM Inventory i JOIN i.variant v JOIN v.product p " +
            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR " +
            "     (:status = 'OUT_OF_STOCK' AND (i.quantity - i.reservedQuantity) <= 0) OR " +
            "     (:status = 'LOW_STOCK' AND (i.quantity - i.reservedQuantity) > 0 AND (i.quantity - i.reservedQuantity) <= i.lowStockThreshold) OR " +
            "     (:status = 'IN_STOCK' AND (i.quantity - i.reservedQuantity) > i.lowStockThreshold))")
    org.springframework.data.domain.Page<Inventory> findAllInventories(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("status") String status,
            Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE (i.quantity - i.reservedQuantity) <= i.lowStockThreshold AND (i.quantity - i.reservedQuantity) > 0")
    long countLowStock();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE (i.quantity - i.reservedQuantity) <= 0")
    long countOutOfStock();

    @Query("SELECT i FROM Inventory i JOIN FETCH i.variant v JOIN FETCH v.product p WHERE (i.quantity - i.reservedQuantity) <= i.lowStockThreshold ORDER BY (i.quantity - i.reservedQuantity) ASC")
    List<Inventory> findLowStockInventories(Pageable pageable);
}
