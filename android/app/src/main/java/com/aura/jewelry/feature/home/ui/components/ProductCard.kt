package com.aura.jewelry.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aura.jewelry.core.designsystem.AuraBadgeShape
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldContainer
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraImageShape
import com.aura.jewelry.core.designsystem.AuraSakura300
import com.aura.jewelry.core.designsystem.AuraSakura400
import com.aura.jewelry.core.designsystem.AuraSakuraContainer
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.core.network.model.ProductSummaryDto
import java.text.NumberFormat
import java.util.Locale

/**
 * Luxury Product Card Component.
 * Faithfully matches Web ProductCard.tsx with:
 * - Dark Glassmorphism container (16dp radius, 0.8dp subtle border)
 * - Image area with dark scrim and 12dp radius
 * - Translucent category pill & discount badge with Sakura glow
 * - Circular frosted Wishlist heart button
 * - 5-Star rating row in Sakura gold
 * - Gold 400 price highlight and strikethrough original price
 * - Clean editorial typography
 */
@Composable
fun ProductCard(
    product: ProductSummaryDto,
    isWishlisted: Boolean,
    onProductClick: (String) -> Unit,
    onToggleWishlist: (ProductSummaryDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val formattedMinPrice = currencyFormatter.format(product.minPrice)
    val hasDiscount = product.maxPrice > product.minPrice
    val formattedOriginalPrice = if (hasDiscount) currencyFormatter.format(product.maxPrice) else null

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(AuraCardShape)
            .border(0.8.dp, AuraBorderDark, AuraCardShape)
            .clickable { onProductClick(product.slug) },
        shape = AuraCardShape,
        color = AuraCardDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 1. Image Area with Badges & Wishlist Overlay (matches web: 12px radius, #0a0c12 background)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(AuraImageShape)
                    .background(Color(0xFF08090D))
                    .border(0.5.dp, Color(0x12FFFFFF), AuraImageShape)
            ) {
                AsyncImage(
                    model = product.thumbnailUrl ?: "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500&auto=format&fit=crop",
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top Left: Badges (Discount & Category Pill)
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (product.isFeatured) {
                        Box(
                            modifier = Modifier
                                .clip(AuraBadgeShape)
                                .background(AuraGoldContainer)
                                .border(0.5.dp, AuraGoldPrimary.copy(alpha = 0.5f), AuraBadgeShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FEATURED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = AuraGoldPrimary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    if (product.categoryName != null) {
                        Box(
                            modifier = Modifier
                                .clip(AuraBadgeShape)
                                .background(Color(0xCC050508))
                                .border(0.5.dp, Color(0x26FFFFFF), AuraBadgeShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = product.categoryName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = AuraSakura300,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }
                }

                // Top Right: Frosted Wishlist Heart Button (matches web: 32px circle, frosted glass)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xA6050508))
                        .border(0.5.dp, Color(0x26FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { onToggleWishlist(product) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Yêu thích",
                            tint = if (isWishlisted) AuraSakuraPrimary else AuraTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Star Rating Row (5 Stars in Sakura-400 + "(5.0)" matching web line 191)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = AuraSakura400,
                        modifier = Modifier.size(11.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(5.0)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AuraTextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Product Title
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                ),
                color = AuraTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold
            )

            // 4. Material Overview
            if (!product.materialOverview.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.materialOverview,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = AuraTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Divider Line (matches web: borderTop rgba(255, 255, 255, 0.06))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Color(0x12FFFFFF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 6. Pricing & Action Row (Price in Gold 400, original price strikethrough)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formattedMinPrice,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AuraGold400
                    )
                    if (formattedOriginalPrice != null) {
                        Text(
                            text = formattedOriginalPrice,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = AuraTextMuted
                        )
                    }
                }

                // Quick View / Bag Pill Indicator
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0x14FFFFFF))
                        .border(0.5.dp, Color(0x26FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Xem nhanh",
                        tint = AuraSakura300,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

