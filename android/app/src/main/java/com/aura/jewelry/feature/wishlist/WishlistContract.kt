package com.aura.jewelry.feature.wishlist

import com.aura.jewelry.core.database.model.WishlistEntity

data class WishlistUiState(
    val items: List<WishlistEntity> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null
) {
    val totalCount: Int get() = items.size
}

sealed interface WishlistUiIntent {
    data class RemoveItem(val productId: Long) : WishlistUiIntent
    data object ClearAll : WishlistUiIntent
    data class MoveToBag(val item: WishlistEntity) : WishlistUiIntent
    data object DismissMessage : WishlistUiIntent
}

sealed interface WishlistUiEvent {
    data class NavigateToProduct(val slug: String) : WishlistUiEvent
    data class ShowSnackbar(val message: String) : WishlistUiEvent
}
