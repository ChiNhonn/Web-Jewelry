package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.CreateProductImageRequest;
import com.jewelry.store.dto.request.CreateProductRequest;
import com.jewelry.store.dto.request.CreateProductVariantRequest;
import com.jewelry.store.dto.request.UpdateProductRequest;
import com.jewelry.store.dto.response.ProductDetailResponse;
import com.jewelry.store.entity.*;
import com.jewelry.store.entity.enums.ProductStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.mapper.ProductMapper;
import com.jewelry.store.repository.CategoryRepository;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.repository.ProductVariantRepository;
import com.jewelry.store.service.ProductService;
import com.jewelry.store.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.ProductFilterRequest;
import com.jewelry.store.dto.response.ProductSummaryResponse;
import com.jewelry.store.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDetailResponse createProduct(CreateProductRequest request) {
        log.info("Creating new product with name: {}, baseSku: {}", request.getName(), request.getBaseSku());

        if (productRepository.existsByBaseSku(request.getBaseSku())) {
            throw new BadRequestException("Mã Base SKU '" + request.getBaseSku() + "' đã tồn tại trong hệ thống.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        String slug = generateUniqueSlug(request.getName(), null);

        Product product = Product.builder()
                .category(category)
                .name(request.getName().trim())
                .slug(slug)
                .baseSku(request.getBaseSku().trim().toUpperCase())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .materialOverview(request.getMaterialOverview())
                .brand(request.getBrand() != null ? request.getBrand().trim() : "AURA JEWELRY")
                .isFeatured(request.isFeatured())
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.ACTIVE)
                .build();

        // 1. Process Product Variants & Initial Inventories
        for (CreateProductVariantRequest vReq : request.getVariants()) {
            if (variantRepository.existsBySku(vReq.getSku())) {
                throw new BadRequestException("Mã SKU biến thể '" + vReq.getSku() + "' đã tồn tại.");
            }

            ProductVariant variant = ProductVariant.builder()
                    .sku(vReq.getSku().trim().toUpperCase())
                    .sizeOption(vReq.getSizeOption())
                    .materialType(vReq.getMaterialType())
                    .originalPrice(vReq.getOriginalPrice())
                    .salePrice(vReq.getSalePrice())
                    .status(vReq.getStatus() != null ? vReq.getStatus() : ProductStatus.ACTIVE)
                    .build();

            // Khởi tạo bản ghi Tồn kho kèm Optimistic Lock version = 0L
            Inventory inventory = Inventory.builder()
                    .quantity(vReq.getInitialStock() != null ? vReq.getInitialStock() : 0)
                    .reservedQuantity(0)
                    .lowStockThreshold(vReq.getLowStockThreshold() != null ? vReq.getLowStockThreshold() : 5)
                    .version(0L)
                    .build();

            variant.setInventory(inventory);
            product.addVariant(variant);
        }

        // 2. Process Product Images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (CreateProductImageRequest imgReq : request.getImages()) {
                ProductImage image = ProductImage.builder()
                        .imageUrl(imgReq.getImageUrl())
                        .publicId(imgReq.getPublicId())
                        .isThumbnail(imgReq.isThumbnail())
                        .sortOrder(imgReq.getSortOrder())
                        .build();
                product.addImage(image);
            }
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDetailResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductDetailResponse updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        if (!product.getName().equalsIgnoreCase(request.getName().trim())) {
            String newSlug = generateUniqueSlug(request.getName(), id);
            product.setSlug(newSlug);
        }

        product.setCategory(category);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setMaterialOverview(request.getMaterialOverview());
        if (request.getBrand() != null) product.setBrand(request.getBrand().trim());
        if (request.getIsFeatured() != null) product.setFeatured(request.getIsFeatured());
        if (request.getStatus() != null) product.setStatus(request.getStatus());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDetailResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Soft-deleting or deactivating product id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Soft delete: chuyển sang trạng thái INACTIVE để đảm bảo tính toàn vẹn dữ liệu đơn hàng cũ
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetailBySlug(String slug) {
        Product product = productRepository.findDetailBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));

        // Tải danh sách biến thể kèm tồn kho
        List<ProductVariant> variants = variantRepository.findAllByProductIdWithInventory(product.getId());
        product.setVariants(variants);

        return productMapper.toProductDetailResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetailById(Long id) {
        Product product = productRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        List<ProductVariant> variants = variantRepository.findAllByProductIdWithInventory(product.getId());
        product.setVariants(variants);

        return productMapper.toProductDetailResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> filterProducts(ProductFilterRequest filterRequest) {
        String sortBy = filterRequest.getSortBy();
        String sortDir = filterRequest.getSortDirection();

        Sort sort;
        if ("name".equalsIgnoreCase(sortBy)) {
            sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by("name").ascending() : Sort.by("name").descending();
        } else {
            sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        ProductSpecification spec = new ProductSpecification(filterRequest);

        Page<Product> page = productRepository.findAll(spec, pageable);
        List<ProductSummaryResponse> summaries = productMapper.toProductSummaryResponseList(page.getContent());

        return PageResponse.<ProductSummaryResponse>builder()
                .content(summaries)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private String generateUniqueSlug(String name, Long currentProductId) {
        String baseSlug = SlugUtils.toSlug(name);
        String candidateSlug = baseSlug;
        int counter = 1;

        while (true) {
            var existing = productRepository.findBySlug(candidateSlug);
            if (existing.isEmpty() || (currentProductId != null && existing.get().getId().equals(currentProductId))) {
                return candidateSlug;
            }
            candidateSlug = baseSlug + "-" + counter++;
        }
    }
}
