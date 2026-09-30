package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.response.ProductDetailResponse;
import com.jewelry.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog (Public)", description = "Public endpoints for browsing jewelry products and detailed specs")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Search and filter products", description = "Tìm kiếm và lọc sản phẩm theo từ khóa, danh mục, khoảng giá, chất liệu và phân trang")
    public ResponseEntity<com.jewelry.store.dto.common.ApiResponse<com.jewelry.store.dto.common.PageResponse<com.jewelry.store.dto.response.ProductSummaryResponse>>> filterProducts(
            @jakarta.validation.Valid com.jewelry.store.dto.request.ProductFilterRequest filterRequest) {
        com.jewelry.store.dto.common.PageResponse<com.jewelry.store.dto.response.ProductSummaryResponse> response = 
                productService.filterProducts(filterRequest);
        return ResponseEntity.ok(com.jewelry.store.dto.common.ApiResponse.success(response));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get product detail by slug", description = "Lấy chi tiết sản phẩm trang sức, bộ ảnh gallery và các biến thể size/giá")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(@PathVariable String slug) {
        ProductDetailResponse response = productService.getProductDetailBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
