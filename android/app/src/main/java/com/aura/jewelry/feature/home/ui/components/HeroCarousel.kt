package com.aura.jewelry.feature.home.ui.components

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraSakura300
import com.aura.jewelry.core.designsystem.AuraSakuraButton
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraSerifFontFamily
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.feature.home.HeroBanner

@Composable
fun HeroCarousel(
    banners: List<HeroBanner>,
    onBannerClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
        ) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AuraBackgroundDark)
            ) {
                // Banner Image
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = banner.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Cinematic Multi-Stop Dark Scrim (exact match with web hero gradient)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x66050508),
                                    Color(0xD9050508),
                                    Color(0xFC050508)
                                )
                            )
                        )
                )

                // Editorial Content Overlay (matches web typography layout)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Top Subtitle Badge with Sakura Dash (matches web: 24px pink dash + uppercase tracking)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(2.dp)
                                .background(AuraSakuraPrimary, CircleShape)
                        )
                        Text(
                            text = banner.tag.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.6.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = AuraSakura300
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = banner.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = AuraSerifFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            lineHeight = 30.sp
                        ),
                        color = AuraTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = banner.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp
                        ),
                        color = AuraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sakura Pill Button (.btn-pill-sakura matching web)
                    AuraSakuraButton(
                        onClick = { onBannerClick(banner.targetCategorySlug) },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = "Khám phá bộ sưu tập",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pager Dot Indicators (matches web active dot with Sakura glow)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(3.5.dp)
                        .width(if (isSelected) 22.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) AuraSakuraPrimary else AuraTextMuted.copy(alpha = 0.4f))
                )
            }
        }
    }
}

