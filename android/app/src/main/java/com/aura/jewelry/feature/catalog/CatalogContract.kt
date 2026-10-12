package com.aura.jewelry.feature.catalog

import com.aura.jewelry.core.network.model.CategoryDto
import com.aura.jewelry.core.network.model.ProductSummaryDto

enum class SortOption(
    val title: String,
    val sortBy: String,
    val direction: String
) {
    NEWEST("Mới nhất", "createdAt", "DESC"),
    PRICE_ASC("Giá tăng dần", "price", "ASC"),
    PRICE_DESC("Giá giảm dần", "price", "DESC"),
    BEST_SELLING("Bán chạy nhất", "isFeatured", "DESC")
}

data class PriceRangePreset(
    val title: String,
    val minPrice: Double?,
    val maxPrice: Double?
)

val PRICE_PRESETS = listOf(
    PriceRangePreset("Tất cả mức giá", null, null),
    PriceRangePreset("Dưới 20 triệu", null, 20_000_000.0),
    PriceRangePreset("20 - 50 triệu", 20_000_000.0, 50_000_000.0),
    PriceRangePreset("50 - 100 triệu", 50_000_000.0, 100_000_000.0),
    PriceRangePreset("Trên 100 triệu", 100_000_000.0, null)
)

data class CatalogUiState(
    val searchQuery: String = "",
    val selectedCategorySlug: String? = null,
    val categories: List<CategoryDto> = emptyList(),
    val selectedPricePreset: PriceRangePreset = PRICE_PRESETS.first(),
    val isFeaturedOnly: Boolean = false,
    val selectedSort: SortOption = SortOption.NEWEST,
    val products: List<ProductSummaryDto> = emptyList(),
    val wishlistIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFilterSheetOpen: Boolean = false,
    val totalElements: Long = 0L,
    val errorMessage: String? = null
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedCategorySlug != null) count++
            if (selectedPricePreset.minPrice != null || selectedPricePreset.maxPrice != null) count++
            if (isFeaturedOnly) count++
            if (selectedSort != SortOption.NEWEST) count++
            return count
        }
}

sealed interface CatalogUiIntent {
    data class UpdateSearchQuery(val query: String) : CatalogUiIntent
    data class SelectCategory(val slug: String?) : CatalogUiIntent
    data class SelectPricePreset(val preset: PriceRangePreset) : CatalogUiIntent
    data class ToggleFeaturedOnly(val enabled: Boolean) : CatalogUiIntent
    data class SelectSort(val sort: SortOption) : CatalogUiIntent
    data object OpenFilterSheet : CatalogUiIntent
    data object CloseFilterSheet : CatalogUiIntent
    data object ResetFilters : CatalogUiIntent
    data class ToggleWishlist(val product: ProductSummaryDto) : CatalogUiIntent
    data object Refresh : CatalogUiIntent
    data object DismissError : CatalogUiIntent
}

sealed interface CatalogUiEvent {
    data class NavigateToDetail(val slug: String) : CatalogUiEvent
    data class ShowSnackbar(val message: String) : CatalogUiEvent
}
