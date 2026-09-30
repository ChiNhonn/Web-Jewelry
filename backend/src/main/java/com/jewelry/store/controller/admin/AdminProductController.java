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

    @GetMapping
    @Operation(summary = "List all products for admin", description = "Lấy danh sách tất cả sản phẩm kèm phân trang và tìm kiếm theo từ khóa")
    public ResponseEntity<ApiResponse<com.jewelry.store.dto.common.PageResponse<com.jewelry.store.dto.response.ProductSummaryResponse>>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        com.jewelry.store.dto.request.ProductFilterRequest filter = com.jewelry.store.dto.request.ProductFilterRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .page(page)
                .size(size)
                .build();
        var response = productService.filterProducts(filter);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/variants")
    @Operation(summary = "Add variant to product", description = "Thêm biến thể kích thước/chất liệu mới vào sản phẩm đã có")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> addVariant(
            @PathVariable Long id,
            @Valid @RequestBody com.jewelry.store.dto.request.CreateProductVariantRequest request) {
        ProductDetailResponse response = productService.addVariant(id, request);
        return new ResponseEntity<>(ApiResponse.success("Thêm biến thể thành công!", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Update variant details", description = "Cập nhật giá bán, chất liệu, size của biến thể")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateVariant(
            @PathVariable Long id,
            @PathVariable Long variantId,
            @Valid @RequestBody com.jewelry.store.dto.request.UpdateVariantRequest request) {
        ProductDetailResponse response = productService.updateVariant(id, variantId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật biến thể thành công!", response));
    }

    @DeleteMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Deactivate variant", description = "Vô hiệu hóa biến thể của sản phẩm")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable Long id,
            @PathVariable Long variantId) {
        productService.deleteVariant(id, variantId);
        return ResponseEntity.ok(ApiResponse.empty("Đã vô hiệu hóa biến thể thành công!"));
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Add product image", description = "Thêm ảnh mới vào bộ sưu tập ảnh của sản phẩm")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> addProductImage(
            @PathVariable Long id,
            @Valid @RequestBody com.jewelry.store.dto.request.CreateProductImageRequest request) {
        ProductDetailResponse response = productService.addProductImage(id, request);
        return new ResponseEntity<>(ApiResponse.success("Thêm ảnh sản phẩm thành công!", response), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @Operation(summary = "Delete product image", description = "Xóa ảnh khỏi sản phẩm")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        productService.deleteProductImage(id, imageId);
        return ResponseEntity.ok(ApiResponse.empty("Đã xóa ảnh thành công!"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate/Soft-delete product", description = "Chuyển trạng thái sản phẩm sang INACTIVE")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.empty("Đã vô hiệu hóa sản phẩm thành công!"));
    }
}
