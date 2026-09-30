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
}
