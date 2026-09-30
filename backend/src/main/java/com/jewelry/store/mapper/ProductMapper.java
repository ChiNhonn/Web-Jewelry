package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.ProductDetailResponse;
import com.jewelry.store.dto.response.ProductImageResponse;
import com.jewelry.store.dto.response.ProductSummaryResponse;
import com.jewelry.store.dto.response.ProductVariantResponse;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.ProductImage;
import com.jewelry.store.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categorySlug", source = "category.slug")
    @Mapping(target = "thumbnailUrl", source = "images", qualifiedByName = "extractThumbnailUrl")
    @Mapping(target = "minPrice", source = "variants", qualifiedByName = "calculateMinPrice")
    @Mapping(target = "maxPrice", source = "variants", qualifiedByName = "calculateMaxPrice")
    @Mapping(target = "isFeatured", expression = "java(product.isFeatured())")
    ProductSummaryResponse toProductSummaryResponse(Product product);

    List<ProductSummaryResponse> toProductSummaryResponseList(List<Product> products);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "variants", source = "variants")
    @Mapping(target = "isFeatured", expression = "java(product.isFeatured())")
    ProductDetailResponse toProductDetailResponse(Product product);

    @Mapping(target = "stockQuantity", expression = "java(variant.getInventory() != null ? variant.getInventory().getQuantity() : 0)")
    @Mapping(target = "availableQuantity", expression = "java(variant.getInventory() != null ? variant.getInventory().getAvailableQuantity() : 0)")
    ProductVariantResponse toProductVariantResponse(ProductVariant variant);

    List<ProductVariantResponse> toProductVariantResponseList(List<ProductVariant> variants);

    @Mapping(target = "isThumbnail", expression = "java(image.isThumbnail())")
    ProductImageResponse toProductImageResponse(ProductImage image);

    List<ProductImageResponse> toProductImageResponseList(List<ProductImage> images);

    @Named("extractThumbnailUrl")
    default String extractThumbnailUrl(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .filter(ProductImage::isThumbnail)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(images.get(0).getImageUrl());
    }

    @Named("calculateMinPrice")
    default BigDecimal calculateMinPrice(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return variants.stream()
                .map(ProductVariant::getSalePrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    @Named("calculateMaxPrice")
    default BigDecimal calculateMaxPrice(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return variants.stream()
                .map(ProductVariant::getSalePrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }
}
