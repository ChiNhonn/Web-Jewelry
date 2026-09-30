package com.jewelry.store.repository;

import com.jewelry.store.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT w FROM Wishlist w " +
           "JOIN FETCH w.product p " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.variants v " +
           "WHERE w.user.id = :userId " +
           "ORDER BY w.createdAt DESC")
    List<Wishlist> findAllByUserIdWithProductDetails(@Param("userId") Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}
