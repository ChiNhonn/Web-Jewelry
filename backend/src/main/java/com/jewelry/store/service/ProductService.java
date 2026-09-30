package com.jewelry.store.service;

import com.jewelry.store.dto.request.CreateProductRequest;
import com.jewelry.store.dto.request.UpdateProductRequest;
import com.jewelry.store.dto.response.ProductDetailResponse;

public interface ProductService {

    ProductDetailResponse createProduct(CreateProductRequest request);

    ProductDetailResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    ProductDetailResponse getProductDetailBySlug(String slug);

    ProductDetailResponse getProductDetailById(Long id);

    ProductDetailResponse addVariant(Long productId, com.jewelry.store.dto.request.CreateProductVariantRequest request);

    ProductDetailResponse updateVariant(Long productId, Long variantId, com.jewelry.store.dto.request.UpdateVariantRequest request);

    void deleteVariant(Long productId, Long variantId);

    ProductDetailResponse addProductImage(Long productId, com.jewelry.store.dto.request.CreateProductImageRequest request);

    void deleteProductImage(Long productId, Long imageId);

    com.jewelry.store.dto.common.PageResponse<com.jewelry.store.dto.response.ProductSummaryResponse> filterProducts(
            com.jewelry.store.dto.request.ProductFilterRequest filterRequest);
}
