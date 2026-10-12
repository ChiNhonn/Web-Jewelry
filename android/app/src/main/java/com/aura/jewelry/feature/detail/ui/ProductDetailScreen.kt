package com.aura.jewelry.feature.detail.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBackgroundPure
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.designsystem.AuraSakura400
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.feature.detail.ProductDetailUiEffect
import com.aura.jewelry.feature.detail.ProductDetailUiIntent
import com.aura.jewelry.feature.detail.ProductDetailViewModel
import com.aura.jewelry.feature.detail.ui.components.DetailImageGallery
import com.aura.jewelry.feature.detail.ui.components.DetailStickyBottomBar
import com.aura.jewelry.feature.detail.ui.components.ProductSpecsSection
import com.aura.jewelry.feature.detail.ui.components.VariantSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    slug: String,
    viewModel: ProductDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(slug) {
        viewModel.onIntent(ProductDetailUiIntent.LoadProduct(slug))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ProductDetailUiEffect.NavigateBack -> onNavigateBack()
                ProductDetailUiEffect.NavigateToCart -> onNavigateToCart()
                ProductDetailUiEffect.NavigateToCheckout -> onNavigateToCheckout()
                is ProductDetailUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.product?.name ?: "Chi Tiết Tuyệt Tác",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = com.aura.jewelry.core.designsystem.AuraSerifFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AuraGoldPrimary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = AuraGoldPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "Chiêm ngưỡng tuyệt tác ${uiState.product?.name}: https://aurajewelry.com/products/$slug")
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Chia sẻ trang sức"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Chia sẻ",
                            tint = AuraTextSecondary
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Giỏ hàng",
                            tint = AuraGoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.aura.jewelry.core.designsystem.AuraBackgroundPure,
                    titleContentColor = AuraGoldPrimary
                ),
                modifier = Modifier.border(0.5.dp, AuraBorderDark)
            )
        },
        bottomBar = {
            if (uiState.product != null) {
                DetailStickyBottomBar(
                    displayPrice = uiState.displayPrice,
                    originalPrice = uiState.originalPrice,
                    isWishlisted = uiState.isWishlisted,
                    isOutOfStock = uiState.isOutOfStock,
                    isAddingToCart = uiState.isAddingToCart,
                    onToggleWishlist = { viewModel.onIntent(ProductDetailUiIntent.ToggleWishlist) },
                    onAddToCart = { viewModel.onIntent(ProductDetailUiIntent.AddToCart(navigateToCheckout = false)) },
                    onBuyNow = { viewModel.onIntent(ProductDetailUiIntent.AddToCart(navigateToCheckout = true)) }
                )
            }
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
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AuraGoldPrimary, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Đang tải dữ liệu tuyệt tác...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuraTextSecondary
                        )
                    }
                }

                uiState.errorMessage != null && uiState.product == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = AuraTextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { viewModel.onIntent(ProductDetailUiIntent.LoadProduct(slug)) }
                        ) {
                            Text(text = "Thử lại", color = AuraGoldPrimary)
                        }
                    }
                }

                uiState.product != null -> {
                    val product = uiState.product!!
                    val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("vi", "VN"))
                    val hasDiscount = uiState.originalPrice != null && uiState.originalPrice!! > uiState.displayPrice

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 1. Image Gallery
                        item {
                            DetailImageGallery(
                                images = product.images,
                                selectedImageIndex = uiState.selectedImageIndex,
                                onImageSelected = { viewModel.onIntent(ProductDetailUiIntent.SelectImage(it)) }
                            )
                        }

                        // 2. Title, Ratings & Price Box (matching web lines 368-418)
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = product.brand?.uppercase() ?: "AURA HAUTE JOAILLERIE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            letterSpacing = 1.6.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = com.aura.jewelry.core.designsystem.AuraSakura400
                                    )
                                    Text(
                                        text = product.category?.name ?: "Trang Sức",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AuraTextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontFamily = com.aura.jewelry.core.designsystem.AuraSerifFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        lineHeight = 30.sp
                                    ),
                                    color = AuraTextPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Star Rating Row (matching web lines 377-390)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    repeat(5) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = com.aura.jewelry.core.designsystem.AuraSakura400,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "5.0",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = AuraTextPrimary
                                    )
                                    Text(
                                        text = " • 18 Khách Hàng VIP Đã Đánh Giá",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AuraTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Luxury Price Box (matches web lines 393-418)
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(0.8.dp, AuraBorderDark, com.aura.jewelry.core.designsystem.AuraCardShape),
                                    shape = com.aura.jewelry.core.designsystem.AuraCardShape,
                                    color = com.aura.jewelry.core.designsystem.AuraCardDark
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = currencyFormatter.format(uiState.displayPrice),
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = com.aura.jewelry.core.designsystem.AuraGold400
                                        )

                                        if (hasDiscount) {
                                            Text(
                                                text = currencyFormatter.format(uiState.originalPrice!!),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                                ),
                                                color = AuraTextMuted
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .clip(com.aura.jewelry.core.designsystem.AuraBadgeShape)
                                                    .background(com.aura.jewelry.core.designsystem.AuraSakuraContainer)
                                                    .border(0.5.dp, com.aura.jewelry.core.designsystem.AuraBorderSakura, com.aura.jewelry.core.designsystem.AuraBadgeShape)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "TIẾT KIỆM " + currencyFormatter.format(uiState.originalPrice!! - uiState.displayPrice),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = com.aura.jewelry.core.designsystem.AuraSakura300,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                if (!product.shortDescription.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = product.shortDescription,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                        color = AuraTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = AuraBorderDark)
                            }
                        }

                        // 3. Interactive Variant Selector
                        item {
                            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                VariantSelector(
                                    variants = product.variants,
                                    selectedVariant = uiState.selectedVariant,
                                    quantity = uiState.quantity,
                                    onVariantSelected = { viewModel.onIntent(ProductDetailUiIntent.SelectVariant(it)) },
                                    onQuantityChanged = { viewModel.onIntent(ProductDetailUiIntent.UpdateQuantity(it)) }
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                HorizontalDivider(color = AuraBorderDark)
                            }
                        }

                        // 4. Specifications & GIA Assurance
                        item {
                            Box(modifier = Modifier.padding(20.dp)) {
                                ProductSpecsSection(
                                    product = product,
                                    selectedVariant = uiState.selectedVariant
                                )
                            }
                        }

                        // Bottom Spacer for sticky bar
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}
