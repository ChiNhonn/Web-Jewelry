package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.*;
import com.jewelry.store.dto.response.*;
import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.enums.ProductStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.CartItemRepository;
import com.jewelry.store.repository.CategoryRepository;
import com.jewelry.store.repository.FavoriteRepository;
import com.jewelry.store.repository.OrderDetailRepository;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.service.PriceBook;
import com.jewelry.store.service.ProductService;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartItemRepository cartItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final PriceBook priceBook;

    @Override
    @Transactional
    public ProductDetailResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
        CreateProductVariantRequest variant = request.getVariants().get(0);
        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(variant.getOriginalPrice())
                .quantity(variant.getInitialStock() == null ? 0 : variant.getInitialStock())
                .category(category)
                .material(firstText(request.getMaterialOverview(), variant.getMaterialType()))
                .weight(request.getWeightGrams() == null ? null : request.getWeightGrams().stripTrailingZeros().toPlainString() + " g")
                .gemType(request.getStoneType())
                .purity(request.getClarity())
                .size(variant.getSizeOption())
                .warranty(request.getWarranty())
                .imageName(request.getImages() == null || request.getImages().isEmpty() ? null : request.getImages().get(0).getImageUrl())
                .createdAt(LocalDateTime.now())
                .build();
        product = productRepository.save(product);
        priceBook.saveProductDiscount(product, variant.getOriginalPrice(), variant.getSalePrice());
        return toDetail(product);
    }

    @Override
    @Transactional
    public ProductDetailResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục")));
        }
        if (request.getName() != null) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getMaterialOverview() != null) {
            product.setMaterial(request.getMaterialOverview());
        }
        if (request.getWeightGrams() != null) {
            product.setWeight(request.getWeightGrams().stripTrailingZeros().toPlainString() + " g");
        }
        if (request.getStoneType() != null) {
            product.setGemType(request.getStoneType());
        }
        if (request.getClarity() != null) {
            product.setPurity(request.getClarity());
        }
        if (request.getWarranty() != null) {
            product.setWarranty(request.getWarranty());
        }
        if (request.getStatus() == ProductStatus.INACTIVE) {
            product.setQuantity(0);
        }
        return toDetail(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        product.setQuantity(0);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void purgeProduct(Long id) {
        if (orderDetailRepository.existsByProductId(id)) {
            throw new BadRequestException("Sản phẩm đã có trong đơn hàng. Hãy ngừng bán thay vì xóa.");
        }
        cartItemRepository.findAll().stream()
                .filter(item -> item.getProduct().getId().equals(id))
                .forEach(cartItemRepository::delete);
        favoriteRepository.findAll().stream()
                .filter(item -> item.getProduct().getId().equals(id))
                .forEach(favoriteRepository::delete);
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetailBySlug(String slug) {
        return toDetail(findBySlug(slug));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetailById(Long id) {
        return toDetail(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm")));
    }

    @Override
    @Transactional
    public ProductDetailResponse addVariant(Long productId, CreateProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        if (request.getOriginalPrice() != null) {
            product.setPrice(request.getOriginalPrice());
        }
        if (request.getInitialStock() != null) {
            product.setQuantity(request.getInitialStock());
        }
        if (request.getSizeOption() != null) {
            product.setSize(request.getSizeOption());
        }
        if (request.getMaterialType() != null) {
            product.setMaterial(request.getMaterialType());
        }
        productRepository.save(product);
        priceBook.saveProductDiscount(product, request.getOriginalPrice(), request.getSalePrice());
        return toDetail(product);
    }

    @Override
    @Transactional
    public ProductDetailResponse updateVariant(Long productId, Long variantId, UpdateVariantRequest request) {
        Product current = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        CreateProductVariantRequest create = CreateProductVariantRequest.builder()
                .sku("SP-" + productId)
                .originalPrice(request.getOriginalPrice() == null ? current.getPrice() : request.getOriginalPrice())
                .salePrice(request.getSalePrice() == null ? current.getPrice() : request.getSalePrice())
                .sizeOption(request.getSizeOption())
                .materialType(request.getMaterialType())
                .initialStock(current.getQuantity())
                .build();
        return addVariant(productId, create);
    }

    @Override
    public void deleteVariant(Long productId, Long variantId) {
        throw new BadRequestException("Sản phẩm trong cơ sở dữ liệu này chỉ có một giá và một tồn kho.");
    }

    @Override
    @Transactional
    public ProductDetailResponse addProductImage(Long productId, CreateProductImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        product.setImageName(request.getImageUrl());
        return toDetail(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProductImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        product.setImageName(null);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> filterProducts(ProductFilterRequest filter) {
        String keyword = filter.getKeyword() == null ? "" : filter.getKeyword().trim().toLowerCase(Locale.ROOT);
        Long categoryId = filter.getCategoryId() != null
                ? filter.getCategoryId()
                : StoreText.categoryIdFromSlug(filter.getCategorySlug());
        List<ProductSummaryResponse> matched = productRepository.findAll().stream()
                .filter(product -> keyword.isEmpty()
                        || product.getName().toLowerCase(Locale.ROOT).contains(keyword)
                        || (product.getMaterial() != null && product.getMaterial().toLowerCase(Locale.ROOT).contains(keyword)))
                .filter(product -> categoryId == null || product.getCategory().getId().equals(categoryId))
                .filter(product -> filter.getMinPrice() == null || product.getPrice().compareTo(filter.getMinPrice()) >= 0)
                .filter(product -> filter.getMaxPrice() == null || product.getPrice().compareTo(filter.getMaxPrice()) <= 0)
                .filter(product -> !Boolean.TRUE.equals(filter.getInStockOnly()) || product.getQuantity() > 0)
                .map(this::toSummary)
                .toList();
        Comparator<ProductSummaryResponse> comparator = Comparator.comparing(ProductSummaryResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        if ("minPrice".equals(filter.getSortBy()) || "price".equals(filter.getSortBy())) {
            comparator = Comparator.comparing(ProductSummaryResponse::getMinPrice);
        } else if ("name".equals(filter.getSortBy())) {
            comparator = Comparator.comparing(ProductSummaryResponse::getName);
        }
        if ("desc".equalsIgnoreCase(filter.getSortDirection()) && ("minPrice".equals(filter.getSortBy()) || "name".equals(filter.getSortBy()))) {
            comparator = comparator.reversed();
        }
        List<ProductSummaryResponse> sorted = matched.stream().sorted(comparator).toList();
        int page = Math.max(filter.getPage(), 0);
        int size = filter.getSize() <= 0 ? 12 : filter.getSize();
        int from = Math.min(page * size, sorted.size());
        int to = Math.min(from + size, sorted.size());
        List<ProductSummaryResponse> slice = sorted.subList(from, to);
        int totalPages = size == 0 ? 1 : (int) Math.ceil(sorted.size() / (double) size);
        return PageResponse.<ProductSummaryResponse>builder()
                .content(slice)
                .page(page)
                .size(size)
                .totalElements(sorted.size())
                .totalPages(Math.max(totalPages, 1))
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }

    private Product findBySlug(String slug) {
        Long id = StoreText.productIdFromSlug(slug);
        if (id == null) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    }

    private ProductSummaryResponse toSummary(Product product) {
        PriceBook.Quote quote = priceBook.quote(product);
        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(StoreText.productSlug(product.getId()))
                .baseSku("SP-" + product.getId())
                .categoryName(product.getCategory().getName())
                .categorySlug(StoreText.categorySlug(product.getCategory().getId(), product.getCategory().getName()))
                .brand("AURA")
                .materialOverview(product.getMaterial())
                .isFeatured(false)
                .minPrice(quote.sale())
                .maxPrice(quote.sale())
                .originalPrice(quote.onPromotion() ? quote.original() : null)
                .defaultVariantId(product.getId())
                .thumbnailUrl(StoreText.imageUrl(product.getImageName()))
                .stockQuantity(product.getQuantity() == null ? 0 : product.getQuantity())
                .status(product.getQuantity() != null && product.getQuantity() > 0 ? ProductStatus.ACTIVE : ProductStatus.OUT_OF_STOCK)
                .createdAt(product.getCreatedAt())
                .build();
    }

    private ProductDetailResponse toDetail(Product product) {
        PriceBook.Quote quote = priceBook.quote(product);
        int stock = product.getQuantity() == null ? 0 : product.getQuantity();
        ProductStatus status = stock > 0 ? ProductStatus.ACTIVE : ProductStatus.OUT_OF_STOCK;
        Category category = product.getCategory();
        ProductVariantResponse variant = ProductVariantResponse.builder()
                .id(product.getId())
                .sku("SP-" + product.getId())
                .sizeOption(product.getSize())
                .materialType(product.getMaterial())
                .originalPrice(quote.original())
                .salePrice(quote.sale())
                .status(status)
                .stockQuantity(stock)
                .availableQuantity(stock)
                .build();
        String image = StoreText.imageUrl(product.getImageName());
        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(StoreText.productSlug(product.getId()))
                .baseSku("SP-" + product.getId())
                .category(CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(StoreText.categorySlug(category.getId(), category.getName()))
                        .description(category.getDescription())
                        .promotionPercent(priceBook.categoryPercent(category.getId()))
                        .build())
                .description(product.getDescription())
                .materialOverview(product.getMaterial())
                .weightLabel(product.getWeight())
                .stoneType(emptyToNull(product.getGemType()))
                .clarity(emptyToNull(product.getPurity()))
                .warranty(emptyToNull(product.getWarranty()))
                .brand("AURA")
                .isFeatured(false)
                .status(status)
                .images(image == null ? List.of() : List.of(ProductImageResponse.builder()
                        .id(product.getId())
                        .imageUrl(image)
                        .isThumbnail(true)
                        .sortOrder(0)
                        .build()))
                .variants(List.of(variant))
                .createdAt(product.getCreatedAt())
                .build();
    }

    private String firstText(String left, String right) {
        if (left != null && !left.isBlank()) {
            return left;
        }
        return right;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
