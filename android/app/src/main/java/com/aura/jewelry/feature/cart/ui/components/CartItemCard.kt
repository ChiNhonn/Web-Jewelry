package com.aura.jewelry.feature.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraErrorRed
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.core.network.model.CartItemDto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartItemCard(
    item: CartItemDto,
    isUpdating: Boolean,
    onProductClick: (String) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val unitPriceFormatted = currencyFormatter.format(item.unitPrice)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(0.8.dp, AuraBorderDark, com.aura.jewelry.core.designsystem.AuraCardShape),
        shape = com.aura.jewelry.core.designsystem.AuraCardShape,
        color = com.aura.jewelry.core.designsystem.AuraCardDark
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(com.aura.jewelry.core.designsystem.AuraImageShape)
                    .background(Color(0xFF08090D))
                    .border(0.5.dp, Color(0x14FFFFFF), com.aura.jewelry.core.designsystem.AuraImageShape)
                    .clickable { onProductClick(item.productSlug) }
            ) {
                AsyncImage(
                    model = item.thumbnailUrl ?: "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=300&auto=format&fit=crop",
                    contentDescription = item.productName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Product Name
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = AuraTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onProductClick(item.productSlug) }
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Variant Specification Badges
                val variantDesc = listOfNotNull(
                    item.materialType,
                    item.sizeOption?.let { "Size: $it" }
                ).joinToString(" • ")

                Text(
                    text = if (variantDesc.isNotEmpty()) variantDesc else "Tiêu chuẩn",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = com.aura.jewelry.core.designsystem.AuraSakura300
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Unit Price
                Text(
                    text = unitPriceFormatted,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = com.aura.jewelry.core.designsystem.AuraGold400
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stepper + Delete Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Stepper (pill shape matching web)
                    Surface(
                        color = Color(0x0DFFFFFF),
                        shape = com.aura.jewelry.core.designsystem.AuraPillShape,
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, AuraBorderDark)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = { onQuantityChange(item.quantity - 1) },
                                enabled = !isUpdating,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Giảm",
                                    tint = if (item.quantity > 1) AuraTextPrimary else AuraTextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                            }

                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp)
                                        .size(12.dp),
                                    color = com.aura.jewelry.core.designsystem.AuraGold400,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "${item.quantity}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = AuraTextPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }

                            IconButton(
                                onClick = { onQuantityChange(item.quantity + 1) },
                                enabled = !isUpdating && (item.availableStock == 0 || item.quantity < item.availableStock),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tăng",
                                    tint = AuraTextPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // Delete Item Button
                    IconButton(
                        onClick = onRemoveClick,
                        enabled = !isUpdating,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Xóa khỏi túi",
                            tint = AuraErrorRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
