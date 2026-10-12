package com.aura.jewelry.feature.detail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.network.model.ProductImageDto
import kotlinx.coroutines.launch

@Composable
fun DetailImageGallery(
    images: List<ProductImageDto>,
    selectedImageIndex: Int,
    onImageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFF07080D)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Chưa có hình ảnh", color = Color.Gray)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { images.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedImageIndex) {
        if (selectedImageIndex in images.indices && pagerState.currentPage != selectedImageIndex) {
            pagerState.animateScrollToPage(selectedImageIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onImageSelected(pagerState.currentPage)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Main Image Pager with Pinch-to-Zoom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFF07080D))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                var scale by remember { mutableFloatStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
                    scale = (scale * zoomChange).coerceIn(1f, 3.5f)
                    offset += offsetChange
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformableState)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = if (scale > 1f) offset.x else 0f,
                            translationY = if (scale > 1f) offset.y else 0f
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = images[page].imageUrl,
                        contentDescription = "Chi tiết trang sức ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Image Counter Pill
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp),
                shape = CircleShape,
                color = Color(0x99050508),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, AuraBorderDark)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${images.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraTextPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Thumbnail Preview Strip
        if (images.size > 1) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(images) { index, img ->
                    val isSelected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(AuraSurfaceDark)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) AuraGoldPrimary else AuraBorderDark,
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    ) {
                        AsyncImage(
                            model = img.imageUrl,
                            contentDescription = "Ảnh thu nhỏ ${index + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
