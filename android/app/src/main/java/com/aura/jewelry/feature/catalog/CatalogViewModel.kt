package com.aura.jewelry.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.network.model.ProductSummaryDto
import com.aura.jewelry.data.repository.ProductRepository
import com.aura.jewelry.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState(isLoading = true))
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CatalogUiEvent>()
    val uiEvent: SharedFlow<CatalogUiEvent> = _uiEvent.asSharedFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        loadCategories()
        observeWishlist()
        setupSearchDebounce()
        loadProducts()
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            wishlistRepository.getWishlist().collect { items ->
                val ids = items.map { it.productId }.toSet()
                _uiState.update { it.copy(wishlistIds = ids) }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            productRepository.getCategories().collect { result ->
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(categories = result.data) }
                }
            }
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(400L)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(searchQuery = query) }
                    loadProducts()
                }
        }
    }

    fun onIntent(intent: CatalogUiIntent) {
        when (intent) {
            is CatalogUiIntent.UpdateSearchQuery -> {
                searchQueryFlow.value = intent.query
            }
            is CatalogUiIntent.SelectCategory -> {
                _uiState.update { it.copy(selectedCategorySlug = intent.slug) }
                loadProducts()
            }
            is CatalogUiIntent.SelectPricePreset -> {
                _uiState.update { it.copy(selectedPricePreset = intent.preset) }
                loadProducts()
            }
            is CatalogUiIntent.ToggleFeaturedOnly -> {
                _uiState.update { it.copy(isFeaturedOnly = intent.enabled) }
                loadProducts()
            }
            is CatalogUiIntent.SelectSort -> {
                _uiState.update { it.copy(selectedSort = intent.sort) }
                loadProducts()
            }
            is CatalogUiIntent.OpenFilterSheet -> {
                _uiState.update { it.copy(isFilterSheetOpen = true) }
            }
            is CatalogUiIntent.CloseFilterSheet -> {
                _uiState.update { it.copy(isFilterSheetOpen = false) }
            }
            is CatalogUiIntent.ResetFilters -> {
                _uiState.update {
                    it.copy(
                        selectedCategorySlug = null,
                        selectedPricePreset = PRICE_PRESETS.first(),
                        isFeaturedOnly = false,
                        selectedSort = SortOption.NEWEST
                    )
                }
                loadProducts()
            }
            is CatalogUiIntent.ToggleWishlist -> {
                toggleWishlist(intent.product)
            }
            is CatalogUiIntent.Refresh -> {
                loadProducts(isRefresh = true)
            }
            is CatalogUiIntent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun toggleWishlist(product: ProductSummaryDto) {
        viewModelScope.launch {
            val added = wishlistRepository.toggleWishlist(
                productId = product.id,
                name = product.name,
                slug = product.slug,
                price = product.minPrice,
                thumbnailUrl = product.thumbnailUrl
            )
            val msg = if (added) "Đã thêm vào danh sách yêu thích" else "Đã xóa khỏi danh sách yêu thích"
            _uiEvent.emit(CatalogUiEvent.ShowSnackbar(msg))
        }
    }

    private fun loadProducts(isRefresh: Boolean = false) {
        val state = _uiState.value
        val keyword = state.searchQuery.ifBlank { null }
        val categorySlug = state.selectedCategorySlug
        val minPrice = state.selectedPricePreset.minPrice
        val maxPrice = state.selectedPricePreset.maxPrice
        val isFeatured = if (state.isFeaturedOnly) true else null
        val sortBy = state.selectedSort.sortBy
        val direction = state.selectedSort.direction

        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            productRepository.filterProducts(
                keyword = keyword,
                categorySlug = categorySlug,
                minPrice = minPrice,
                maxPrice = maxPrice,
                isFeatured = isFeatured,
                sortBy = sortBy,
                direction = direction,
                page = 0,
                size = 50
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        // Handled by state
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                products = result.data.content,
                                totalElements = result.data.totalElements,
                                errorMessage = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}
