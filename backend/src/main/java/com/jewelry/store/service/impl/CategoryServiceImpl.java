package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.CreateCategoryRequest;
import com.jewelry.store.dto.request.UpdateCategoryRequest;
import com.jewelry.store.dto.response.CategoryResponse;
import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.enums.CategoryStatus;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.CategoryRepository;
import com.jewelry.store.service.CategoryService;
import com.jewelry.store.service.PriceBook;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final PriceBook priceBook;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = categoryRepository.save(Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build());
        priceBook.saveCategoryPercent(category.getId(), category.getName(), request.getPromotionPercent());
        return toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
        if (request.getName() != null) {
            category.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        categoryRepository.save(category);
        if (request.getPromotionPercent() != null) {
            priceBook.saveCategoryPercent(category.getId(), category.getName(), request.getPromotionPercent());
        }
        return toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Long id = StoreText.categoryIdFromSlug(slug);
        if (id != null) {
            return categoryRepository.findById(id).map(this::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
        }
        return categoryRepository.findAll().stream()
                .filter(category -> StoreText.slugify(category.getName()).equalsIgnoreCase(slug))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return toResponse(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryHierarchy() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(StoreText.categorySlug(category.getId(), category.getName()))
                .description(category.getDescription())
                .promotionPercent(priceBook.categoryPercent(category.getId()))
                .status(CategoryStatus.ACTIVE)
                .build();
    }
}
