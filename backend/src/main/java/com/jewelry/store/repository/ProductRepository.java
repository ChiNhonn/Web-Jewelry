package com.jewelry.store.repository;

import com.jewelry.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findByBaseSku(String baseSku);

    boolean existsBySlug(String slug);

    boolean existsByBaseSku(String baseSku);

    /**
     * Eagerly fetch product detail with its category and images to prevent N+1 queries.
     */
    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.images " +
           "WHERE p.slug = :slug")
    Optional<Product> findDetailBySlug(@Param("slug") String slug);

    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.images " +
           "WHERE p.id = :id")
    Optional<Product> findDetailById(@Param("id") Long id);
}
