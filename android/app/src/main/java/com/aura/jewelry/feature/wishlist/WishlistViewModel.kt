package com.aura.jewelry.feature.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.database.model.WishlistEntity
import com.aura.jewelry.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState(isLoading = true))
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<WishlistUiEvent>()
    val uiEvent: SharedFlow<WishlistUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeWishlist()
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            wishlistRepository.getWishlist()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, message = e.localizedMessage) }
                }
                .collect { wishlistItems ->
                    _uiState.update {
                        it.copy(
                            items = wishlistItems,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onIntent(intent: WishlistUiIntent) {
        when (intent) {
            is WishlistUiIntent.RemoveItem -> {
                viewModelScope.launch {
                    wishlistRepository.removeFromWishlist(intent.productId)
                    _uiEvent.emit(WishlistUiEvent.ShowSnackbar("Đã xóa khỏi danh sách yêu thích"))
                }
            }
            is WishlistUiIntent.ClearAll -> {
                viewModelScope.launch {
                    wishlistRepository.clearWishlist()
                    _uiEvent.emit(WishlistUiEvent.ShowSnackbar("Đã làm trống danh sách yêu thích"))
                }
            }
            is WishlistUiIntent.MoveToBag -> {
                viewModelScope.launch {
                    _uiEvent.emit(WishlistUiEvent.NavigateToProduct(intent.item.slug))
                }
            }
            is WishlistUiIntent.DismissMessage -> {
                _uiState.update { it.copy(message = null) }
            }
        }
    }
}
