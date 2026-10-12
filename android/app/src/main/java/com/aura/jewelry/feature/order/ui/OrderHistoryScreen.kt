package com.aura.jewelry.feature.order.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraDarkBackground
import com.aura.jewelry.core.designsystem.AuraDarkSurface
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraMutedText
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.network.model.OrderSummaryDto
import com.aura.jewelry.feature.order.OrderHistoryUiIntent
import com.aura.jewelry.feature.order.OrderHistoryViewModel
import com.aura.jewelry.feature.order.OrderStatusType
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LỊCH SỬ ĐƠN HÀNG",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.2.sp
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraBackgroundDark
                )
            )
        },
        containerColor = AuraBackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Status Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderStatusType.entries.forEach { status ->
                    val isSelected = uiState.selectedStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onIntent(OrderHistoryUiIntent.FilterByStatus(status)) },
                        label = {
                            Text(
                                text = status.vietnameseTitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White,
                            selectedLabelColor = Color(0xFF050508),
                            containerColor = Color(0xFF0E1017),
                            labelColor = AuraMutedText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = AuraBorderSubtle,
                            selectedBorderColor = Color.White
                        ),
                        shape = AuraPillShape
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.onIntent(OrderHistoryUiIntent.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading && uiState.orders.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AuraGoldPrimary)
                        }
                    }
                    uiState.orders.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    tint = AuraMutedText,
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    text = "Không có đơn hàng nào",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Bạn chưa có đơn hàng nào trong trạng thái này",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AuraMutedText
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.orders, key = { it.id }) { order ->
                                OrderSummaryCard(
                                    order = order,
                                    currencyFormat = currencyFormat,
                                    onClick = { onNavigateToDetail(order.orderCode) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    order: OrderSummaryDto,
    currencyFormat: NumberFormat,
    onClick: () -> Unit
) {
    val statusEnum = OrderStatusType.fromCode(order.orderStatus)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = AuraCardShape,
        colors = CardDefaults.cardColors(containerColor = AuraCardDark),
        border = BorderStroke(0.8.dp, AuraBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderCode,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AuraGold400
                )

                Card(
                    shape = AuraPillShape,
                    colors = CardDefaults.cardColors(containerColor = statusEnum.color.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, statusEnum.color.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = statusEnum.vietnameseTitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusEnum.color,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            order.createdAt?.let {
                Text(
                    text = "Ngày đặt: $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraMutedText
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.totalQuantity} sản phẩm • ${order.paymentMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currencyFormat.format(order.totalAmount),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AuraGold400
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AuraMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
