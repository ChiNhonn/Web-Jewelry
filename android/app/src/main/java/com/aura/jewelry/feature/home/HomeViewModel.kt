package com.aura.jewelry.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.network.model.ProductSummaryDto
import com.aura.jewelry.data.repository.CartRepository
import com.aura.jewelry.data.repository.ProductRepository
import com.aura.jewelry.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            banners = listOf(
                HeroBanner(
                    id = "b1",
                    title = "BỘ SƯU TẬP SOLITAIRE",
                    subtitle = "Kim Cương Tự Nhiên Giác Cắt Hoàn Hảo",
                    tag = "HAUTE JOAILLERIE",
                    imageUrl = "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=800&auto=format&fit=crop",
                    targetCategorySlug = "nhan-kim-cuong"
                ),
                HeroBanner(
                    id = "b2",
                    title = "ROYAL SAPPHIRE & GOLD",
                    subtitle = "Tuyệt Tác Vàng 18K Đính Đá Quý Hoàng Gia",
                    tag = "EXCLUSIVE MASTERPIECE",
                    imageUrl = "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=800&auto=format&fit=crop",
                    targetCategorySlug = "day-chuyen"
                ),
                HeroBanner(
                    id = "b3",
                    title = "SAKURA DIAMOND GLOW",
                    subtitle = "Sắc Hồng Quý Phái Dành Riêng Cho Bạn",
                    tag = "NEW ARRIVALS 2026",
                    imageUrl = "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=800&auto=format&fit=crop",
                    targetCategorySlug = "vong-tay"
                )
            )
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<HomeUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeWishlist()
        loadData(isRefresh = false)
        fetchCartCount()
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            wishlistRepository.getWishlist().collect { wishlistItems ->
                _uiState.update { current ->
                    current.copy(wishlistIds = wishlistItems.map { it.productId }.toSet())
                }
            }
        }
    }

    fun onIntent(intent: HomeUiIntent) {
        when (intent) {
            HomeUiIntent.LoadInitialData -> loadData(isRefresh = false)
            HomeUiIntent.RefreshData -> loadData(isRefresh = true)
            is HomeUiIntent.SelectCategory -> {
                _uiState.update { it.copy(selectedCategorySlug = intent.categorySlug) }
                fetchCatalogProducts(intent.categorySlug)
            }
            is HomeUiIntent.ToggleWishlist -> toggleWishlist(intent.product)
            HomeUiIntent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadData(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            // 1. Fetch Categories
            launch {
                productRepository.getCategories().collect { result ->
                    if (result is NetworkResult.Success) {
                        _uiState.update { it.copy(categories = result.data) }
                    }
                }
            }

            // 2. Fetch Featured Products
            launch {
                productRepository.filterProducts(isFeatured = true, size = 6).collect { result ->
                    if (result is NetworkResult.Success) {
                        _uiState.update { it.copy(featuredProducts = result.data.content) }
                    }
                }
            }

            // 3. Fetch Catalog Products (All or Selected Category)
            fetchCatalogProducts(_uiState.value.selectedCategorySlug)

            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    private fun fetchCatalogProducts(categorySlug: String?) {
        viewModelScope.launch {
            productRepository.filterProducts(categorySlug = categorySlug, size = 12).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(catalogProducts = result.data.content) }
                    }
                    is NetworkResult.Error -> {
                        // Offline or network error handled by repository cache
                    }
                    is NetworkResult.Loading -> {}
                }
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
            val msg = if (added) "Đã thêm vào Danh sách Yêu thích" else "Đã xóa khỏi Danh sách Yêu thích"
            _uiEffect.send(HomeUiEffect.ShowToast(msg))
        }
    }

    private fun fetchCartCount() {
        viewModelScope.launch {
            cartRepository.getCart().collect { result ->
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(cartItemCount = result.data.totalQuantity) }
                }
            }
        }
    }
}
