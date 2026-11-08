package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.ProductDetailResponse;
import com.jewelry.store.dto.response.ProductImageResponse;
import com.jewelry.store.dto.response.ProductSummaryResponse;
import com.jewelry.store.dto.response.ProductVariantResponse;
import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.ProductImage;
import com.jewelry.store.entity.ProductVariant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-08T19:42:08+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ProductSummaryResponse toProductSummaryResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductSummaryResponse.ProductSummaryResponseBuilder productSummaryResponse = ProductSummaryResponse.builder();

        productSummaryResponse.categoryName( productCategoryName( product ) );
        productSummaryResponse.categorySlug( productCategorySlug( product ) );
        productSummaryResponse.thumbnailUrl( extractThumbnailUrl( product.getImages() ) );
        productSummaryResponse.minPrice( calculateMinPrice( product.getVariants() ) );
        productSummaryResponse.maxPrice( calculateMaxPrice( product.getVariants() ) );
        productSummaryResponse.baseSku( product.getBaseSku() );
        productSummaryResponse.brand( product.getBrand() );
        productSummaryResponse.createdAt( product.getCreatedAt() );
        productSummaryResponse.id( product.getId() );
        productSummaryResponse.materialOverview( product.getMaterialOverview() );
        productSummaryResponse.name( product.getName() );
        productSummaryResponse.slug( product.getSlug() );
        productSummaryResponse.status( product.getStatus() );

        productSummaryResponse.isFeatured( product.isFeatured() );

        return productSummaryResponse.build();
    }

    @Override
    public List<ProductSummaryResponse> toProductSummaryResponseList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductSummaryResponse> list = new ArrayList<ProductSummaryResponse>( products.size() );
        for ( Product product : products ) {
            list.add( toProductSummaryResponse( product ) );
        }

        return list;
    }

    @Override
    public ProductDetailResponse toProductDetailResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDetailResponse.ProductDetailResponseBuilder productDetailResponse = ProductDetailResponse.builder();

        productDetailResponse.category( categoryMapper.toCategoryResponse( product.getCategory() ) );
        productDetailResponse.images( toProductImageResponseList( product.getImages() ) );
        productDetailResponse.variants( toProductVariantResponseList( product.getVariants() ) );
        productDetailResponse.baseSku( product.getBaseSku() );
        productDetailResponse.brand( product.getBrand() );
        productDetailResponse.clarity( product.getClarity() );
        productDetailResponse.createdAt( product.getCreatedAt() );
        productDetailResponse.description( product.getDescription() );
        productDetailResponse.id( product.getId() );
        productDetailResponse.materialOverview( product.getMaterialOverview() );
        productDetailResponse.name( product.getName() );
        productDetailResponse.shortDescription( product.getShortDescription() );
        productDetailResponse.slug( product.getSlug() );
        productDetailResponse.status( product.getStatus() );
        productDetailResponse.stoneType( product.getStoneType() );
        productDetailResponse.updatedAt( product.getUpdatedAt() );
        productDetailResponse.warranty( product.getWarranty() );
        productDetailResponse.weightGrams( product.getWeightGrams() );

        productDetailResponse.isFeatured( product.isFeatured() );

        return productDetailResponse.build();
    }

    @Override
    public ProductVariantResponse toProductVariantResponse(ProductVariant variant) {
        if ( variant == null ) {
            return null;
        }

        ProductVariantResponse.ProductVariantResponseBuilder productVariantResponse = ProductVariantResponse.builder();

        productVariantResponse.id( variant.getId() );
        productVariantResponse.materialType( variant.getMaterialType() );
        productVariantResponse.originalPrice( variant.getOriginalPrice() );
        productVariantResponse.sizeOption( variant.getSizeOption() );
        productVariantResponse.sku( variant.getSku() );
        productVariantResponse.status( variant.getStatus() );

        productVariantResponse.salePrice( com.jewelry.store.util.PricingUtils.effectiveSale(variant) );
        productVariantResponse.stockQuantity( variant.getInventory() != null ? variant.getInventory().getQuantity() : 0 );
        productVariantResponse.availableQuantity( variant.getInventory() != null ? variant.getInventory().getAvailableQuantity() : 0 );

        return productVariantResponse.build();
    }

    @Override
    public List<ProductVariantResponse> toProductVariantResponseList(List<ProductVariant> variants) {
        if ( variants == null ) {
            return null;
        }

        List<ProductVariantResponse> list = new ArrayList<ProductVariantResponse>( variants.size() );
        for ( ProductVariant productVariant : variants ) {
            list.add( toProductVariantResponse( productVariant ) );
        }

        return list;
    }

    @Override
    public ProductImageResponse toProductImageResponse(ProductImage image) {
        if ( image == null ) {
            return null;
        }

        ProductImageResponse.ProductImageResponseBuilder productImageResponse = ProductImageResponse.builder();

        productImageResponse.id( image.getId() );
        productImageResponse.imageUrl( image.getImageUrl() );
        productImageResponse.publicId( image.getPublicId() );
        productImageResponse.sortOrder( image.getSortOrder() );

        productImageResponse.isThumbnail( image.isThumbnail() );

        return productImageResponse.build();
    }

    @Override
    public List<ProductImageResponse> toProductImageResponseList(List<ProductImage> images) {
        if ( images == null ) {
            return null;
        }

        List<ProductImageResponse> list = new ArrayList<ProductImageResponse>( images.size() );
        for ( ProductImage productImage : images ) {
            list.add( toProductImageResponse( productImage ) );
        }

        return list;
    }

    private String productCategoryName(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String productCategorySlug(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String slug = category.getSlug();
        if ( slug == null ) {
            return null;
        }
        return slug;
    }
}
