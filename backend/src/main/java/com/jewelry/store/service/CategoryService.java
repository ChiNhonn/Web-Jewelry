package com.jewelry.store.service;

import com.jewelry.store.dto.request.CreateCategoryRequest;
import com.jewelry.store.dto.request.UpdateCategoryRequest;
import com.jewelry.store.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse getCategoryBySlug(String slug);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getCategoryHierarchy();
}
