package com.aura.jewelry.feature.order.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraDarkBackground
import com.aura.jewelry.core.designsystem.AuraDarkSurface
import com.aura.jewelry.core.designsystem.AuraDarkSurfaceVariant
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraMutedText
import com.aura.jewelry.core.designsystem.AuraSakura
import com.aura.jewelry.core.network.model.OrderItemDto
import com.aura.jewelry.core.network.model.OrderResponseDto
import com.aura.jewelry.feature.order.OrderDetailUiEvent
import com.aura.jewelry.feature.order.OrderDetailUiIntent
import com.aura.jewelry.feature.order.OrderDetailViewModel
import com.aura.jewelry.feature.order.OrderStatusType
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderCode: String,
    viewModel: OrderDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    LaunchedEffect(orderCode) {
        viewModel.onIntent(OrderDetailUiIntent.LoadOrder(orderCode))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is OrderDetailUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is OrderDetailUiEvent.OrderCancelledSuccess -> {
                    // State will reflect cancelled status automatically
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CHI TIẾT ĐƠN HÀNG",
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AuraBackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.order == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AuraGoldPrimary
                    )
                }
                uiState.order != null -> {
                    val order = uiState.order!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Order Status Banner Card
                        OrderStatusBanner(order = order)

                        // Shipping Address Card
                        OrderShippingAddressCard(order = order)

                        // Ordered Items Card
                        OrderItemsCard(items = order.items, currencyFormat = currencyFormat)

                        // Payment & Price Breakdown Card
                        OrderPaymentSummaryCard(order = order, currencyFormat = currencyFormat)

                        // Action: Hủy đơn nếu còn PENDING
                        if (order.orderStatus.equals("PENDING", ignoreCase = true)) {
                            OutlinedButton(
                                onClick = { viewModel.onIntent(OrderDetailUiIntent.ShowCancelDialog) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, AuraSakura),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AuraSakura
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "YÊU CẦU HỦY ĐƠN HÀNG",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "Không tìm thấy thông tin đơn hàng",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onIntent(OrderDetailUiIntent.LoadOrder(orderCode)) },
                            colors = ButtonDefaults.buttonColors(containerColor = AuraGoldPrimary, contentColor = Color.Black)
                        ) {
                            Text("THỬ LẠI")
                        }
                    }
                }
            }

            // Cancel Order Confirmation Dialog
            if (uiState.showCancelDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.onIntent(OrderDetailUiIntent.DismissCancelDialog) },
                    containerColor = AuraDarkSurface,
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            text = "Xác nhận hủy đơn hàng?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "Quý khách có chắc chắn muốn hủy đơn hàng mã ${uiState.orderCode} không?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AuraMutedText
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = uiState.cancelReason,
                                onValueChange = { viewModel.onIntent(OrderDetailUiIntent.UpdateCancelReason(it)) },
                                label = { Text("Lý do hủy đơn (tùy chọn)") },
                                placeholder = { Text("Đổi ý, muốn đổi size nhẫn khác...") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AuraGoldPrimary,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedLabelColor = AuraGoldPrimary,
                                    unfocusedLabelColor = AuraMutedText
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.onIntent(OrderDetailUiIntent.ConfirmCancelOrder) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraSakura,
                                contentColor = Color.White
                            )
                        ) {
                            Text("XÁC NHẬN HỦY")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onIntent(OrderDetailUiIntent.DismissCancelDialog) }) {
                            Text("GIỮ ĐƠN", color = AuraMutedText)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OrderStatusBanner(order: OrderResponseDto) {
    val statusEnum = OrderStatusType.fromCode(order.orderStatus)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkSurface),
        border = BorderStroke(1.dp, statusEnum.color.copy(alpha = 0.5f))
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
                Column {
                    Text(
                        text = "MÃ ĐƠN HÀNG",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraMutedText,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = order.orderCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AuraGoldPrimary
                    )
                }

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = statusEnum.color.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, statusEnum.color.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = statusEnum.vietnameseTitle.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusEnum.color,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            if (!order.createdAt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AuraMutedText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Thời gian tạo: ${order.createdAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraMutedText
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderShippingAddressCard(order: OrderResponseDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkSurface),
        border = BorderStroke(1.dp, AuraGoldPrimary.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AuraGoldPrimary
                )
                Text(
                    text = "ĐỊA CHỈ NHẬN TRANG SỨC",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            order.customerName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            order.customerPhone?.let {
                Text(
                    text = "Điện thoại: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraMutedText
                )
            }

            order.shippingAddressSnapshot?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!order.customerNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ghi chú: ${order.customerNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraGoldPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun OrderItemsCard(
    items: List<OrderItemDto>,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkSurface),
        border = BorderStroke(1.dp, AuraGoldPrimary.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = AuraGoldPrimary
                )
                Text(
                    text = "DANH SÁCH SẢN PHẨM (${items.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraDarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!item.thumbnailUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = item.thumbnailUrl,
                                    contentDescription = item.productName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(50.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = null,
                                    tint = AuraGoldPrimary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.productName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            val variant = buildString {
                                item.materialType?.let { append(it) }
                                if (item.sizeOption != null) {
                                    if (isNotEmpty()) append(" • ")
                                    append("Size ${item.sizeOption}")
                                }
                            }
                            if (variant.isNotBlank()) {
                                Text(
                                    text = variant,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AuraMutedText
                                )
                            }
                            Text(
                                text = "Số lượng: x${item.quantity}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AuraGoldPrimary
                            )
                        }

                        Text(
                            text = currencyFormat.format(item.subtotal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderPaymentSummaryCard(
    order: OrderResponseDto,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = AuraGold400
                )
                Text(
                    text = "THANH TOÁN & CHI PHÍ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Phương thức thanh toán
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Phương thức", style = MaterialTheme.typography.bodySmall, color = AuraMutedText)
                Text(
                    text = order.paymentMethod,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Trạng thái thanh toán
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tình trạng thanh toán", style = MaterialTheme.typography.bodySmall, color = AuraMutedText)
                Text(
                    text = order.paymentStatus,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (order.paymentStatus.equals("COMPLETED", ignoreCase = true)) Color(0xFF66BB6A) else Color(0xFFFFA000)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(10.dp))

            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tạm tính", style = MaterialTheme.typography.bodySmall, color = AuraMutedText)
                Text(currencyFormat.format(order.subtotal), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            }

            // Discount
            if (order.discountAmount > 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Giảm giá (${order.couponCode ?: ""})", style = MaterialTheme.typography.bodySmall, color = AuraSakura)
                    Text("-${currencyFormat.format(order.discountAmount)}", style = MaterialTheme.typography.bodySmall, color = AuraSakura, fontWeight = FontWeight.SemiBold)
                }
            }

            // Shipping
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Vận chuyển bảo mật", style = MaterialTheme.typography.bodySmall, color = AuraMutedText)
                Text("MIỄN PHÍ", style = MaterialTheme.typography.bodySmall, color = AuraGold400, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(12.dp))

            // Grand Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TỔNG TIỀN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(currencyFormat.format(order.totalAmount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AuraGold400)
            }
        }
    }
}
