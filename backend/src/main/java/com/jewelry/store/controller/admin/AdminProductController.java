package com.jewelry.store.controller.admin;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.request.CreateProductRequest;
import com.jewelry.store.dto.request.UpdateProductRequest;
import com.jewelry.store.dto.response.ProductDetailResponse;
import com.jewelry.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Product Management (Admin)", description = "Admin endpoints for creating, editing, and managing jewelry products and inventory")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create new jewelry product with variants and images", description = "Tạo mẫu sản phẩm mới cùng các biến thể ni nhẫn/chất liệu và số lượng tồn kho")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDetailResponse response = productService.createProduct(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo sản phẩm thành công!", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details", description = "Cập nhật thông tin mô tả, danh mục, thương hiệu của sản phẩm")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDetailResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công!", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product detail by ID (Admin)", description = "Lấy chi tiết sản phẩm theo ID")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Long id) {
        ProductDetailResponse response = productService.getProductDetailById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate/Soft-delete product", description = "Chuyển trạng thái sản phẩm sang INACTIVE")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.empty("Đã vô hiệu hóa sản phẩm thành công!"));
    }
}
