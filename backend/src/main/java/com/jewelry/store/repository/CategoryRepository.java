package com.jewelry.store.repository;

import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    /**
     * Retrieve top-level root categories (where parent is null)
     * with their subcategories to build the navigation hierarchy.
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL AND c.status = :status ORDER BY c.name ASC")
    List<Category> findRootCategoriesWithChildren(CategoryStatus status);

    List<Category> findAllByStatus(CategoryStatus status);
}
