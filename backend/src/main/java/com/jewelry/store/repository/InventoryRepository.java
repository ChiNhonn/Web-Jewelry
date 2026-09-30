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

    @Query("SELECT COUNT(i) FROM Inventory i WHERE (i.quantity - i.reservedQuantity) <= i.lowStockThreshold AND (i.quantity - i.reservedQuantity) > 0")
    long countLowStock();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE (i.quantity - i.reservedQuantity) <= 0")
    long countOutOfStock();

    @Query("SELECT i FROM Inventory i JOIN FETCH i.variant v JOIN FETCH v.product p WHERE (i.quantity - i.reservedQuantity) <= i.lowStockThreshold ORDER BY (i.quantity - i.reservedQuantity) ASC")
    List<Inventory> findLowStockInventories(Pageable pageable);
}
