package com.aura.jewelry.feature.home.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraSurfaceDark

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFF10131E),
        Color(0xFF1E2436),
        Color(0xFF10131E)
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    )
}

@Composable
fun ProductCardSkeleton(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .border(0.5.dp, AuraBorderDark, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        color = AuraSurfaceDark
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Image skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Category tag
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(10.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Price
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
                    .shimmerEffect()
            )
        }
    }
}
