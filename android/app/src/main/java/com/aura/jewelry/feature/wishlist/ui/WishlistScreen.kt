package com.aura.jewelry.feature.wishlist.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.aura.jewelry.core.database.model.WishlistEntity
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraOutlineButton
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.designsystem.AuraSakuraButton
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraSurfaceVariantDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.feature.wishlist.WishlistUiEvent
import com.aura.jewelry.feature.wishlist.WishlistUiIntent
import com.aura.jewelry.feature.wishlist.WishlistViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCatalog: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is WishlistUiEvent.NavigateToProduct -> onNavigateToProduct(event.slug)
                is WishlistUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DANH SÁCH YÊU THÍCH",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 1.2.sp
                        )
                        if (uiState.totalCount > 0) {
                            Text(
                                text = "(${uiState.totalCount})",
                                style = MaterialTheme.typography.titleSmall,
                                color = AuraGoldPrimary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (uiState.totalCount > 0) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Xóa tất cả",
                                tint = AuraTextMuted
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraBackgroundDark
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AuraBackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AuraGoldPrimary
                    )
                }
                uiState.items.isEmpty() -> {
                    // Empty Wishlist View
                    EmptyWishlistView(
                        onExploreClick = onNavigateToCatalog,
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
                        items(uiState.items, key = { it.productId }) { item ->
                            WishlistGridCard(
                                item = item,
                                currencyFormat = currencyFormat,
                                onItemClick = { onNavigateToProduct(item.slug) },
                                onRemoveClick = { viewModel.onIntent(WishlistUiIntent.RemoveItem(item.productId)) }
                            )
                        }
                    }
                }
            }

            // Clear All Confirmation Dialog
            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    containerColor = AuraSurfaceDark,
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            text = "Xóa toàn bộ yêu thích?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Text(
                            text = "Quý khách có chắc muốn xóa tất cả sản phẩm khỏi danh sách yêu thích không?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuraTextMuted
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.onIntent(WishlistUiIntent.ClearAll)
                                showClearDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraSakuraPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("XÓA TẤT CẢ")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) {
                            Text("HỦY", color = AuraTextMuted)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun WishlistGridCard(
    item: WishlistEntity,
    currencyFormat: NumberFormat,
    onItemClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        shape = AuraCardShape,
        colors = CardDefaults.cardColors(containerColor = AuraCardDark),
        border = BorderStroke(0.8.dp, AuraBorderSubtle)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail with Favorite Heart Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFF07080D))
            ) {
                if (!item.thumbnailUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.thumbnailUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = null,
                            tint = AuraGold400.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Frosted Heart active button top-right
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        .border(0.8.dp, AuraBorderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Bỏ yêu thích",
                        tint = AuraSakuraPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Product Details
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AuraTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currencyFormat.format(item.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AuraGold400
                )

                Spacer(modifier = Modifier.height(10.dp))

                AuraOutlineButton(
                    onClick = onItemClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    borderColor = AuraGold400.copy(alpha = 0.35f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "XEM CHI TIẾT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AuraGold400,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyWishlistView(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AuraCardDark)
                .border(1.2.dp, AuraSakuraPrimary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = AuraSakuraPrimary,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Danh Sách Yêu Thích Trống",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hãy lưu lại những tuyệt tác trang sức kim hoàn quý phái bạn mê đắm để dễ dàng chiêm ngưỡng và sở hữu.",
            style = MaterialTheme.typography.bodyMedium,
            color = AuraTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(26.dp))

        AuraSakuraButton(
            onClick = onExploreClick,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Text(
                text = "KHÁM PHÁ BỘ SƯU TẬP VIP",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.2.sp
            )
        }
    }
}
