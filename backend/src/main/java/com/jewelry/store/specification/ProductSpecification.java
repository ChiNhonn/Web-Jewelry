package com.jewelry.store.specification;

import com.jewelry.store.dto.request.ProductFilterRequest;
import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.ProductVariant;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Query Specification using Spring Data JPA Criteria API.
 * Assembles dynamic SQL WHERE predicates based on user search parameters.
 */
@RequiredArgsConstructor
public class ProductSpecification implements Specification<Product> {

    private final ProductFilterRequest filter;

    @Override
    public Predicate toPredicate(
            @NonNull Root<Product> root,
            @NonNull CriteriaQuery<?> query,
            @NonNull CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();
        boolean requiresVariantJoin = false;

        // 1. Lọc theo từ khóa tìm kiếm (Tên, Base SKU, Mô tả ngắn)
        if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
            String pattern = "%" + filter.getKeyword().trim().toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(root.get("name")), pattern);
            Predicate skuPredicate = cb.like(cb.lower(root.get("baseSku")), pattern);
            Predicate descPredicate = cb.like(cb.lower(root.get("shortDescription")), pattern);
            predicates.add(cb.or(namePredicate, skuPredicate, descPredicate));
        }

        // 2. Lọc theo danh mục sản phẩm (Category ID hoặc Slug)
        if (filter.getCategoryId() != null) {
            Join<Product, Category> categoryJoin = root.join("category", JoinType.INNER);
            predicates.add(cb.equal(categoryJoin.get("id"), filter.getCategoryId()));
        } else if (filter.getCategorySlug() != null && !filter.getCategorySlug().isBlank()) {
            Join<Product, Category> categoryJoin = root.join("category", JoinType.INNER);
            predicates.add(cb.equal(categoryJoin.get("slug"), filter.getCategorySlug().trim()));
        }

        // 3. Lọc theo khoảng giá (Min Price - Max Price trên biến thể)
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            requiresVariantJoin = true;
            Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.INNER);

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(variantJoin.get("salePrice"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(variantJoin.get("salePrice"), filter.getMaxPrice()));
            }
        }

        // 4. Lọc theo chất liệu (Vàng 18K, Bạc 925, Bạch Kim Platinum)
        if (filter.getMaterial() != null && !filter.getMaterial().isBlank()) {
            String matPattern = "%" + filter.getMaterial().trim().toLowerCase() + "%";
            Predicate matOverview = cb.like(cb.lower(root.get("materialOverview")), matPattern);
            predicates.add(matOverview);
        }

        // 5. Lọc theo thương hiệu (Brand)
        if (filter.getBrand() != null && !filter.getBrand().isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("brand")), filter.getBrand().trim().toLowerCase()));
        }

        // 6. Lọc sản phẩm nổi bật (Featured)
        if (filter.getIsFeatured() != null) {
            predicates.add(cb.equal(root.get("isFeatured"), filter.getIsFeatured()));
        }

        // 7. Lọc theo trạng thái kinh doanh
        if (filter.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        // Nếu có JOIN với bảng biến thể, bắt buộc dùng DISTINCT để không bị trùng lặp sản phẩm
        if (requiresVariantJoin) {
            query.distinct(true);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
