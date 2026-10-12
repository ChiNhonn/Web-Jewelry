package com.aura.jewelry.feature.home.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBackgroundPure
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSakura300
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.feature.home.HomeUiEffect
import com.aura.jewelry.feature.home.HomeUiIntent
import com.aura.jewelry.feature.home.HomeViewModel
import com.aura.jewelry.feature.home.ui.components.CategoryChipsRow
import com.aura.jewelry.feature.home.ui.components.HeroCarousel
import com.aura.jewelry.feature.home.ui.components.ProductCard
import com.aura.jewelry.feature.home.ui.components.ProductCardSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.NavigateToDetail -> onNavigateToDetail(effect.slug)
                HomeUiEffect.NavigateToCart -> onNavigateToCart()
                is HomeUiEffect.NavigateToCategory -> viewModel.onIntent(HomeUiIntent.SelectCategory(effect.slug))
                is HomeUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = null,
                            tint = AuraGoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AURA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 3.sp
                                ),
                                color = AuraGoldPrimary
                            )
                            Text(
                                text = "HIGH JEWELRY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    letterSpacing = 1.8.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = AuraSakura300
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(HomeUiIntent.RefreshData) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Làm mới",
                            tint = AuraTextSecondary
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        if (uiState.cartItemCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = AuraSakuraPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = if (uiState.cartItemCount > 99) "99+" else "${uiState.cartItemCount}",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Giỏ hàng",
                                    tint = AuraGoldPrimary
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Giỏ hàng",
                                tint = AuraTextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraBackgroundPure,
                    titleContentColor = AuraGoldPrimary
                ),
                modifier = Modifier.border(0.5.dp, AuraBorderDark)
            )
        },
        containerColor = AuraBackgroundDark,
        modifier = modifier
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.onIntent(HomeUiIntent.RefreshData) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraBackgroundDark),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. Hero Banners Carousel
                item {
                    HeroCarousel(
                        banners = uiState.banners,
                        onBannerClick = { categorySlug ->
                            if (categorySlug != null) {
                                viewModel.onIntent(HomeUiIntent.SelectCategory(categorySlug))
                            }
                        }
                    )
                }

                // 2. Category Chips Row
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryChipsRow(
                        categories = uiState.categories,
                        selectedCategorySlug = uiState.selectedCategorySlug,
                        onCategorySelected = { slug ->
                            viewModel.onIntent(HomeUiIntent.SelectCategory(slug))
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 3. Featured Products Showcase
                if (uiState.featuredProducts.isNotEmpty() || uiState.isLoading) {
                    item {
                        SectionHeader(
                            tag = "Tuyệt Tác Giới Hạn",
                            title = "TUYỆT TÁC NỔI BẬT",
                            subtitle = "Những thiết kế đỉnh cao được chế tác thủ công tinh xảo"
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        if (uiState.isLoading && uiState.featuredProducts.isEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(3) {
                                    ProductCardSkeleton(modifier = Modifier.width(200.dp))
                                }
                            }
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(uiState.featuredProducts, key = { it.id }) { product ->
                                    ProductCard(
                                        product = product,
                                        isWishlisted = uiState.wishlistIds.contains(product.id),
                                        onProductClick = onNavigateToDetail,
                                        onToggleWishlist = { viewModel.onIntent(HomeUiIntent.ToggleWishlist(it)) },
                                        modifier = Modifier.width(200.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                // 4. Catalog Products Grid (Chunked pairs for smooth LazyColumn rendering)
                item {
                    val sectionTitle = if (uiState.selectedCategorySlug == null) {
                        "BỘ SƯU TẬP TRANG SỨC"
                    } else {
                        uiState.categories.find { it.slug == uiState.selectedCategorySlug }?.name?.uppercase()
                            ?: "DANH MỤC TRANG SỨC"
                    }
                    SectionHeader(
                        tag = "Bộ Sưu Tập Tinh Tuyển",
                        title = sectionTitle,
                        subtitle = "Kim cương tự nhiên chuẩn GIA & Vàng Ý 18K độc bản"
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (uiState.isLoading && uiState.catalogProducts.isEmpty()) {
                    items(2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProductCardSkeleton(modifier = Modifier.weight(1f))
                            ProductCardSkeleton(modifier = Modifier.weight(1f))
                        }
                    }
                } else if (uiState.catalogProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hiện chưa có sản phẩm nào trong danh mục này",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AuraTextMuted
                            )
                        }
                    }
                } else {
                    // Chunk products into 2 columns
                    val productPairs = uiState.catalogProducts.chunked(2)
                    items(productPairs.size) { index ->
                        val pair = productPairs[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProductCard(
                                product = pair[0],
                                isWishlisted = uiState.wishlistIds.contains(pair[0].id),
                                onProductClick = onNavigateToDetail,
                                onToggleWishlist = { viewModel.onIntent(HomeUiIntent.ToggleWishlist(it)) },
                                modifier = Modifier.weight(1f)
                            )
                            if (pair.size > 1) {
                                ProductCard(
                                    product = pair[1],
                                    isWishlisted = uiState.wishlistIds.contains(pair[1].id),
                                    onProductClick = onNavigateToDetail,
                                    onToggleWishlist = { viewModel.onIntent(HomeUiIntent.ToggleWishlist(it)) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // 5. VIP Trust Badges Footer
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    VipTrustBadges()
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    tag: String,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .background(AuraSakuraPrimary, androidx.compose.foundation.shape.CircleShape)
            )
            Text(
                text = tag.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = com.aura.jewelry.core.designsystem.AuraSakura400
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = com.aura.jewelry.core.designsystem.AuraSerifFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = AuraTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = AuraTextSecondary
        )
    }
}

@Composable
private fun VipTrustBadges() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(0.8.dp, AuraBorderDark, com.aura.jewelry.core.designsystem.AuraCardShape),
        shape = com.aura.jewelry.core.designsystem.AuraCardShape,
        color = com.aura.jewelry.core.designsystem.AuraCardDark
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TrustBadgeItem(
                icon = Icons.Default.Verified,
                title = "100% Kim Cương Chuẩn Quốc Tế GIA",
                subtitle = "Khắc mã laser bảo chứng & kiểm định nghiêm ngặt"
            )
            TrustBadgeItem(
                icon = Icons.Default.Security,
                title = "Vận Chuyển Hỏa Tốc Bảo Hiểm 100%",
                subtitle = "Bàn giao tận tay an toàn tuyệt đối qua dịch vụ chuyên biệt"
            )
            TrustBadgeItem(
                icon = Icons.Default.Diamond,
                title = "Bảo Hành & Đánh Bóng Trọn Đời",
                subtitle = "Chăm sóc và làm mới trang sức miễn phí vĩnh viễn tại xưởng"
            )
        }
    }
}

@Composable
private fun TrustBadgeItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(com.aura.jewelry.core.designsystem.AuraGoldContainer, com.aura.jewelry.core.designsystem.AuraImageShape)
                .border(0.5.dp, AuraGoldPrimary.copy(alpha = 0.4f), com.aura.jewelry.core.designsystem.AuraImageShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AuraGoldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = AuraTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = AuraTextMuted
            )
        }
    }
}

