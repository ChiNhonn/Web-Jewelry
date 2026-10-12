package com.aura.jewelry.feature.home

import com.aura.jewelry.core.network.model.CategoryDto
import com.aura.jewelry.core.network.model.ProductSummaryDto

/**
 * Editorial Hero Banner item for the luxury carousel.
 */
data class HeroBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val imageUrl: String,
    val targetCategorySlug: String? = null
)

/**
 * UI State for the Home & Discovery screen.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val banners: List<HeroBanner> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val featuredProducts: List<ProductSummaryDto> = emptyList(),
    val catalogProducts: List<ProductSummaryDto> = emptyList(),
    val selectedCategorySlug: String? = null,
    val wishlistIds: Set<Long> = emptySet(),
    val cartItemCount: Int = 0
)

/**
 * UI Intents (User interactions) for Home.
 */
sealed interface HomeUiIntent {
    data object LoadInitialData : HomeUiIntent
    data object RefreshData : HomeUiIntent
    data class SelectCategory(val categorySlug: String?) : HomeUiIntent
    data class ToggleWishlist(val product: ProductSummaryDto) : HomeUiIntent
    data object ClearError : HomeUiIntent
}

/**
 * Navigation and Toast Side Effects.
 */
sealed interface HomeUiEffect {
    data class NavigateToDetail(val slug: String) : HomeUiEffect
    data object NavigateToCart : HomeUiEffect
    data class NavigateToCategory(val slug: String) : HomeUiEffect
    data class ShowToast(val message: String) : HomeUiEffect
}
