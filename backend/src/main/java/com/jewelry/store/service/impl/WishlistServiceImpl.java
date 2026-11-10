package com.jewelry.store.service.impl;

import com.jewelry.store.dto.response.WishlistItemResponse;
import com.jewelry.store.entity.Favorite;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.FavoriteRepository;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.PriceBook;
import com.jewelry.store.service.WishlistService;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PriceBook priceBook;

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public WishlistItemResponse addToWishlist(Long userId, Long productId) {
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> favoriteRepository.save(Favorite.builder()
                        .user(userRepository.findById(userId).orElseThrow())
                        .product(productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm")))
                        .createdAt(LocalDateTime.now())
                        .build()));
        return toResponse(favorite);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private WishlistItemResponse toResponse(Favorite favorite) {
        PriceBook.Quote quote = priceBook.quote(favorite.getProduct());
        return WishlistItemResponse.builder()
                .id(favorite.getId())
                .productId(favorite.getProduct().getId())
                .productName(favorite.getProduct().getName())
                .productSlug(StoreText.productSlug(favorite.getProduct().getId()))
                .thumbnailUrl(StoreText.imageUrl(favorite.getProduct().getImageName()))
                .minPrice(quote.sale())
                .maxPrice(quote.sale())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
