package com.aura.jewelry.feature.checkout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraDarkSurface
import com.aura.jewelry.core.designsystem.AuraDarkSurfaceVariant
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraMutedText
import com.aura.jewelry.core.designsystem.AuraSakura
import com.aura.jewelry.core.network.model.CartItemDto
import com.aura.jewelry.feature.checkout.CheckoutUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutPriceSummary(
    uiState: CheckoutUiState,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AuraCardShape,
        colors = CardDefaults.cardColors(containerColor = AuraCardDark),
        border = BorderStroke(0.8.dp, AuraBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = AuraGold400
                )
                Text(
                    text = "TỔNG KẾT ĐƠN HÀNG",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Danh sách các món trong đơn hàng
            val items = uiState.cart?.items ?: emptyList()
            if (items.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { item ->
                        CheckoutItemRow(item = item, currencyFormat = currencyFormat)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Tạm tính
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tạm tính (${uiState.totalQuantity} món)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuraMutedText
                )
                Text(
                    text = currencyFormat.format(uiState.subtotal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Giảm giá voucher
            if (uiState.appliedDiscount > 0.0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ưu đãi voucher (${uiState.appliedCoupon})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AuraSakura
                    )
                    Text(
                        text = "-${currencyFormat.format(uiState.appliedDiscount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AuraSakura
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Vận chuyển & bảo hiểm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Vận chuyển & Bảo hiểm kim hoàn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AuraMutedText
                    )
                }
                Text(
                    text = "MIỄN PHÍ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AuraGoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(14.dp))

            // Tổng thanh toán
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TỔNG THANH TOÁN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Đã bao gồm VAT và bảo hành",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraMutedText
                    )
                }
                Text(
                    text = currencyFormat.format(uiState.grandTotal),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AuraGold400
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trust Assurance Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AuraDarkSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrustBadge(icon = Icons.Default.Diamond, title = "100% GIA")
                TrustBadge(icon = Icons.Default.Shield, title = "Bảo hiểm 100%")
                TrustBadge(icon = Icons.Default.Lock, title = "Thanh toán an toàn")
            }
        }
    }
}

@Composable
private fun CheckoutItemRow(
    item: CartItemDto,
    currencyFormat: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val imageUrl = item.thumbnailUrl
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AuraDarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = item.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = AuraGoldPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            val variantInfo = buildString {
                item.materialType?.let { append(it) }
                if (item.sizeOption != null) {
                    if (isNotEmpty()) append(" • ")
                    append("Size ${item.sizeOption}")
                }
                if (isNotEmpty()) append(" • ")
                append("x${item.quantity}")
            }
            Text(
                text = variantInfo,
                style = MaterialTheme.typography.labelSmall,
                color = AuraMutedText
            )
        }

        Text(
            text = currencyFormat.format(item.subtotal),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TrustBadge(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AuraGoldPrimary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = AuraMutedText,
            fontSize = 11.sp
        )
    }
}
