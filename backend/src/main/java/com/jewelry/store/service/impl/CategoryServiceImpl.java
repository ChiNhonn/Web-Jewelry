package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.CreateCategoryRequest;
import com.jewelry.store.dto.request.UpdateCategoryRequest;
import com.jewelry.store.dto.response.CategoryResponse;
import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.enums.CategoryStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.mapper.CategoryMapper;
import com.jewelry.store.repository.CategoryRepository;
import com.jewelry.store.service.CategoryService;
import com.jewelry.store.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating new category with name: {}", request.getName());

        String slug = generateUniqueSlug(request.getName(), null);

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", request.getParentId()));
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus() != null ? request.getStatus() : CategoryStatus.ACTIVE)
                .parent(parent)
                .build();

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getName().equalsIgnoreCase(request.getName().trim())) {
            String newSlug = generateUniqueSlug(request.getName(), id);
            category.setSlug(newSlug);
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Một danh mục không thể làm cha của chính nó.");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new BadRequestException("Không thể xóa danh mục đang có danh mục con liên kết.");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryHierarchy() {
        List<Category> rootCategories = categoryRepository.findRootCategoriesWithChildren(CategoryStatus.ACTIVE);
        return categoryMapper.toCategoryResponseList(rootCategories);
    }

    private String generateUniqueSlug(String name, Long currentCategoryId) {
        String baseSlug = SlugUtils.toSlug(name);
        String candidateSlug = baseSlug;
        int counter = 1;

        while (true) {
            var existing = categoryRepository.findBySlug(candidateSlug);
            if (existing.isEmpty() || (currentCategoryId != null && existing.get().getId().equals(currentCategoryId))) {
                return candidateSlug;
            }
            candidateSlug = baseSlug + "-" + counter++;
        }
    }
}
