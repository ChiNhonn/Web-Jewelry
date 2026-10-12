package com.aura.jewelry.feature.detail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.jewelry.core.designsystem.AuraBackgroundDeep
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraOutlineButton
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.designsystem.AuraSakuraButton
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetailStickyBottomBar(
    displayPrice: Double,
    originalPrice: Double?,
    isWishlisted: Boolean,
    isOutOfStock: Boolean,
    isAddingToCart: Boolean,
    onToggleWishlist: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val formattedPrice = currencyFormatter.format(displayPrice)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, AuraBorderDark),
        color = AuraBackgroundDeep,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Price Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GIÁ CHÍNH HÃNG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = AuraTextMuted
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AuraGold400
                        )
                        if (originalPrice != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currencyFormatter.format(originalPrice),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                ),
                                color = AuraTextMuted
                            )
                        }
                    }
                }

                // Wishlist Toggle Heart (frosted circle matching web)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x0DFFFFFF))
                        .border(0.8.dp, AuraBorderDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onToggleWishlist) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Yêu thích",
                            tint = if (isWishlisted) AuraSakuraPrimary else AuraTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            if (isOutOfStock) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = AuraPillShape,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = AuraBorderDark,
                        disabledContentColor = AuraTextMuted
                    )
                ) {
                    Text(
                        text = "TẠM HẾT HÀNG TẠI XƯỞNG",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Add to Bag Button (.btn-outline-pill matching web)
                    AuraOutlineButton(
                        onClick = onAddToCart,
                        enabled = !isAddingToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        if (isAddingToCart) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = AuraGold400,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = AuraTextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "THÊM VÀO GIỎ",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                color = AuraTextPrimary
                            )
                        }
                    }

                    // Buy Now Button (.btn-pill-sakura matching web)
                    AuraSakuraButton(
                        onClick = onBuyNow,
                        enabled = !isAddingToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text(
                            text = "MUA NGAY VIP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

