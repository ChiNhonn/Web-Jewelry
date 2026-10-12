package com.aura.jewelry.feature.cart.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraErrorRed
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.feature.cart.CartUiEffect
import com.aura.jewelry.feature.cart.CartUiIntent
import com.aura.jewelry.feature.cart.CartViewModel
import com.aura.jewelry.feature.cart.ui.components.CartItemCard
import com.aura.jewelry.feature.cart.ui.components.CartSummaryCard
import com.aura.jewelry.feature.cart.ui.components.EmptyCartView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onIntent(CartUiIntent.LoadCart)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                CartUiEffect.NavigateToCheckout -> onNavigateToCheckout()
                CartUiEffect.NavigateToLogin -> onNavigateToLogin()
                CartUiEffect.NavigateToCatalog -> onNavigateToCatalog()
                is CartUiEffect.NavigateToProduct -> onNavigateToProduct(effect.slug)
                is CartUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Clear Cart Confirmation Dialog
    if (uiState.showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(CartUiIntent.SetClearCartDialogVisible(false)) },
            title = {
                Text(
                    text = "Dọn sạch túi đồ?",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuraTextPrimary
                )
            },
            text = {
                Text(
                    text = "Quý khách có chắc chắn muốn xóa toàn bộ sản phẩm khỏi túi mua sắm không?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuraTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(CartUiIntent.ClearCart) }) {
                    Text(text = "Dọn sạch", color = AuraErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(CartUiIntent.SetClearCartDialogVisible(false)) }) {
                    Text(text = "Hủy", color = AuraTextMuted)
                }
            },
            containerColor = AuraSurfaceDark,
            shape = MaterialTheme.shapes.large
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TÚI MUA SẮM",
                        style = MaterialTheme.typography.titleLarge,
                        color = AuraGoldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    if (!uiState.isCartEmpty) {
                        IconButton(onClick = { viewModel.onIntent(CartUiIntent.SetClearCartDialogVisible(true)) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Dọn sạch túi",
                                tint = AuraTextMuted
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraSurfaceDark,
                    titleContentColor = AuraGoldPrimary
                )
            )
        },
        containerColor = AuraBackgroundDark,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AuraBackgroundDark)
        ) {
            when {
                uiState.isLoading && uiState.cart == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AuraGoldPrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(text = "Đang kiểm tra túi đồ...", style = MaterialTheme.typography.bodySmall, color = AuraTextSecondary)
                    }
                }

                uiState.isCartEmpty -> {
                    EmptyCartView(onExploreClick = onNavigateToCatalog)
                }

                uiState.cart != null -> {
                    val cart = uiState.cart!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Cart Items List
                        items(cart.items, key = { it.id }) { item ->
                            CartItemCard(
                                item = item,
                                isUpdating = uiState.isUpdatingItemId == item.id,
                                onProductClick = onNavigateToProduct,
                                onQuantityChange = { newQty ->
                                    viewModel.onIntent(CartUiIntent.UpdateQuantity(item.id, newQty))
                                },
                                onRemoveClick = {
                                    viewModel.onIntent(CartUiIntent.RemoveItem(item.id))
                                }
                            )
                        }

                        // Order Summary & Checkout Card
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            CartSummaryCard(
                                cart = cart,
                                couponCode = uiState.couponCode,
                                couponDiscount = uiState.couponDiscount,
                                couponApplied = uiState.couponApplied,
                                couponMessage = uiState.couponMessage,
                                totalAmount = uiState.totalAmount,
                                onCouponCodeChange = { viewModel.onIntent(CartUiIntent.UpdateCouponCode(it)) },
                                onApplyCoupon = { viewModel.onIntent(CartUiIntent.ApplyCoupon) },
                                onProceedToCheckout = { viewModel.onIntent(CartUiIntent.ProceedToCheckout) }
                            )
                        }

                        // Trust Footnote
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Mọi sản phẩm trang sức đều được đóng gói hộp sang trọng và niêm phong bảo hiểm an toàn tuyệt đối.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AuraTextMuted,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
