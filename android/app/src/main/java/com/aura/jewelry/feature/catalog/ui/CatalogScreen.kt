package com.aura.jewelry.feature.catalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraSurfaceVariantDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.feature.catalog.CatalogUiEvent
import com.aura.jewelry.feature.catalog.CatalogUiIntent
import com.aura.jewelry.feature.catalog.CatalogViewModel
import com.aura.jewelry.feature.catalog.PRICE_PRESETS
import com.aura.jewelry.feature.catalog.ui.components.CatalogSearchBar
import com.aura.jewelry.feature.catalog.ui.components.FilterBottomSheet
import com.aura.jewelry.feature.home.ui.components.ProductCard
import com.aura.jewelry.feature.home.ui.components.ProductCardSkeleton
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CatalogUiEvent.NavigateToDetail -> onNavigateToDetail(event.slug)
                is CatalogUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AuraBackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            CatalogSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onIntent(CatalogUiIntent.UpdateSearchQuery(it)) },
                activeFilterCount = uiState.activeFilterCount,
                onOpenFilterClick = { viewModel.onIntent(CatalogUiIntent.OpenFilterSheet) }
            )

            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Category Chip
                val isAllSelected = uiState.selectedCategorySlug == null
                FilterChip(
                    selected = isAllSelected,
                    onClick = { viewModel.onIntent(CatalogUiIntent.SelectCategory(null)) },
                    label = {
                        Text(
                            text = "Tất cả",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.5.sp,
                                fontWeight = if (isAllSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.White,
                        selectedLabelColor = Color(0xFF050508),
                        containerColor = Color(0x0DFFFFFF),
                        labelColor = com.aura.jewelry.core.designsystem.AuraTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isAllSelected,
                        borderColor = AuraBorderDark,
                        selectedBorderColor = Color.White
                    ),
                    shape = com.aura.jewelry.core.designsystem.AuraPillShape
                )

                uiState.categories.forEach { category ->
                    val isSelected = uiState.selectedCategorySlug == category.slug
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onIntent(CatalogUiIntent.SelectCategory(category.slug)) },
                        label = {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White,
                            selectedLabelColor = Color(0xFF050508),
                            containerColor = Color(0x0DFFFFFF),
                            labelColor = com.aura.jewelry.core.designsystem.AuraTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = AuraBorderDark,
                            selectedBorderColor = Color.White
                        ),
                        shape = com.aura.jewelry.core.designsystem.AuraPillShape
                    )
                }
            }

            // Active Filter Removable Badges (if any active)
            if (uiState.activeFilterCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.selectedPricePreset != PRICE_PRESETS.first()) {
                        InputChip(
                            selected = true,
                            onClick = { viewModel.onIntent(CatalogUiIntent.SelectPricePreset(PRICE_PRESETS.first())) },
                            label = { Text(uiState.selectedPricePreset.title, fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, contentDescription = "Xóa lọc giá", modifier = Modifier.size(14.dp))
                            },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = AuraGoldPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = AuraGoldPrimary
                            )
                        )
                    }

                    if (uiState.isFeaturedOnly) {
                        InputChip(
                            selected = true,
                            onClick = { viewModel.onIntent(CatalogUiIntent.ToggleFeaturedOnly(false)) },
                            label = { Text("Bộ sưu tập nổi bật", fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, contentDescription = "Bỏ lọc nổi bật", modifier = Modifier.size(14.dp))
                            },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = AuraGoldPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = AuraGoldPrimary
                            )
                        )
                    }
                }
            }

            // Product Grid Content
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.onIntent(CatalogUiIntent.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading && uiState.products.isEmpty() -> {
                        // Shimmer Loading Grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(6) {
                                ProductCardSkeleton()
                            }
                        }
                    }
                    uiState.products.isEmpty() -> {
                        // Empty Search View
                        EmptyCatalogView(
                            onResetClick = { viewModel.onIntent(CatalogUiIntent.ResetFilters) },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.products, key = { it.id }) { product ->
                                val isWishlisted = uiState.wishlistIds.contains(product.id)
                                ProductCard(
                                    product = product,
                                    isWishlisted = isWishlisted,
                                    onProductClick = { onNavigateToDetail(product.slug) },
                                    onToggleWishlist = { viewModel.onIntent(CatalogUiIntent.ToggleWishlist(it)) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(24.dp)) }
                        }
                    }
                }
            }
        }

        // Filter Modal Bottom Sheet
        if (uiState.isFilterSheetOpen) {
            FilterBottomSheet(
                uiState = uiState,
                onIntent = viewModel::onIntent,
                sheetState = sheetState,
                onDismissRequest = { viewModel.onIntent(CatalogUiIntent.CloseFilterSheet) }
            )
        }
    }
}

@Composable
private fun EmptyCatalogView(
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(AuraSurfaceVariantDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = AuraTextMuted,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Không tìm thấy tác phẩm phù hợp",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AuraTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Vui lòng thử tìm kiếm bằng từ khóa khác hoặc xóa bớt tiêu chí trong bộ lọc.",
            style = MaterialTheme.typography.bodySmall,
            color = AuraTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onResetClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AuraGoldPrimary,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "ĐẶT LẠI BỘ LỌC",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
