package com.aura.jewelry.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
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
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ProductDetailUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onIntent(intent: ProductDetailUiIntent) {
        when (intent) {
            is ProductDetailUiIntent.LoadProduct -> loadProduct(intent.slug)
            is ProductDetailUiIntent.SelectImage -> {
                _uiState.update { it.copy(selectedImageIndex = intent.index) }
            }
            is ProductDetailUiIntent.SelectVariant -> {
                _uiState.update { current ->
                    current.copy(
                        selectedVariant = intent.variant,
                        quantity = 1
                    )
                }
            }
            is ProductDetailUiIntent.UpdateQuantity -> {
                val maxStock = _uiState.value.selectedVariant?.availableQuantity ?: 10
                val clamped = intent.newQuantity.coerceIn(1, maxOf(1, maxStock))
                _uiState.update { it.copy(quantity = clamped) }
            }
            ProductDetailUiIntent.ToggleWishlist -> toggleWishlist()
            is ProductDetailUiIntent.AddToCart -> addToCart(intent.navigateToCheckout)
            ProductDetailUiIntent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadProduct(slug: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            productRepository.getProductDetail(slug).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val product = result.data
                        val defaultVariant = product.variants.firstOrNull { it.availableQuantity > 0 }
                            ?: product.variants.firstOrNull()

                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                product = product,
                                selectedVariant = defaultVariant,
                                selectedImageIndex = 0,
                                quantity = 1
                            )
                        }

                        // Check Wishlist status
                        launch {
                            wishlistRepository.isWishlisted(product.id).collect { isWishlisted ->
                                _uiState.update { it.copy(isWishlisted = isWishlisted) }
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    private fun toggleWishlist() {
        val product = _uiState.value.product ?: return
        val currentPrice = _uiState.value.displayPrice
        viewModelScope.launch {
            val added = wishlistRepository.toggleWishlist(
                productId = product.id,
                name = product.name,
                slug = product.slug,
                price = currentPrice,
                thumbnailUrl = product.images.firstOrNull()?.imageUrl
            )
            val msg = if (added) "Đã thêm vào Danh sách Yêu thích" else "Đã xóa khỏi Danh sách Yêu thích"
            _uiEffect.send(ProductDetailUiEffect.ShowToast(msg))
        }
    }

    private fun addToCart(navigateToCheckout: Boolean) {
        val variant = _uiState.value.selectedVariant
        if (variant == null) {
            _uiState.update { it.copy(errorMessage = "Vui lòng chọn phân loại sản phẩm") }
            return
        }

        val quantity = _uiState.value.quantity
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToCart = true, errorMessage = null) }
            cartRepository.addToCart(variantId = variant.id, quantity = quantity).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(isAddingToCart = false, addedToCartSuccess = true) }
                        if (navigateToCheckout) {
                            _uiEffect.send(ProductDetailUiEffect.NavigateToCheckout)
                        } else {
                            _uiEffect.send(ProductDetailUiEffect.ShowToast("Đã thêm $quantity sản phẩm vào Túi đồ"))
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isAddingToCart = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }
}
