package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.request.CreateCategoryRequest;
import com.jewelry.store.dto.request.UpdateCategoryRequest;
import com.jewelry.store.dto.response.CategoryResponse;
import com.jewelry.store.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "Endpoints for managing jewelry category hierarchy and public navigation")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get category navigation tree", description = "Returns root categories with nested subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryHierarchy() {
        List<CategoryResponse> hierarchy = categoryService.getCategoryHierarchy();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh mục thành công!", hierarchy));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get category by slug", description = "Tra cứu danh mục chi tiết theo slug URL")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        CategoryResponse response = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create category (Admin/Staff only)", description = "Tạo danh mục mới trong cây phân cấp")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo danh mục thành công!", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update category (Admin/Staff only)", description = "Cập nhật thông tin danh mục")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công!", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Delete category (Admin only)", description = "Xóa danh mục nếu không có danh mục con")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.empty("Xóa danh mục thành công!"));
    }
}
