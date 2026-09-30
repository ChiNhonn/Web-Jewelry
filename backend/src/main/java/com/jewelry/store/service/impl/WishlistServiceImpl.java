package com.jewelry.store.service.impl;

import com.jewelry.store.dto.response.WishlistItemResponse;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.ProductImage;
import com.jewelry.store.entity.ProductVariant;
import com.jewelry.store.entity.User;
import com.jewelry.store.entity.Wishlist;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.repository.WishlistRepository;
import com.jewelry.store.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        List<Wishlist> list = wishlistRepository.findAllByUserIdWithProductDetails(userId);
        return list.stream().map(this::mapToWishlistItemResponse).toList();
    }

    @Override
    @Transactional
    public WishlistItemResponse addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new BadRequestException("Sản phẩm này đã có trong danh sách yêu thích của bạn.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        wishlist = wishlistRepository.save(wishlist);
        return mapToWishlistItemResponse(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private WishlistItemResponse mapToWishlistItemResponse(Wishlist wishlist) {
        Product p = wishlist.getProduct();

        String thumbnailUrl = null;
        if (p.getImages() != null && !p.getImages().isEmpty()) {
            thumbnailUrl = p.getImages().stream()
                    .filter(ProductImage::isThumbnail)
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(p.getImages().get(0).getImageUrl());
        }

        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.ZERO;
        if (p.getVariants() != null && !p.getVariants().isEmpty()) {
            minPrice = p.getVariants().stream().map(ProductVariant::getSalePrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
            maxPrice = p.getVariants().stream().map(ProductVariant::getSalePrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        }

        return WishlistItemResponse.builder()
                .id(wishlist.getId())
                .productId(p.getId())
                .productName(p.getName())
                .productSlug(p.getSlug())
                .thumbnailUrl(thumbnailUrl)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}
