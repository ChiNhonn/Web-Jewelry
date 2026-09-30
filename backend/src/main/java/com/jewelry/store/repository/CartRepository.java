package com.jewelry.store.repository;

import com.jewelry.store.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    /**
     * Eagerly fetch cart with items, variants, inventories, product and images
     * in a single query to eliminate N+1 problem during cart retrieval.
     */
    @Query("SELECT DISTINCT c FROM Cart c " +
           "LEFT JOIN FETCH c.items i " +
           "LEFT JOIN FETCH i.variant v " +
           "LEFT JOIN FETCH v.inventory inv " +
           "LEFT JOIN FETCH v.product p " +
           "LEFT JOIN FETCH p.images img " +
           "WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithDetails(@Param("userId") Long userId);
}
