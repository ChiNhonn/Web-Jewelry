package com.aura.jewelry.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================================
// AURA LUXURY WEB-INSPIRED UI COMPONENTS
// Faithfully reproducing Web styles: .btn-pill-sakura, .btn-outline-pill,
// .btn-pill-white, .luxury-glass-card, section headers, etc.
// ============================================================================

/**
 * Primary Call-To-Action Pill Button (.btn-pill-sakura)
 * Features vibrant Sakura pink gradient, pill shape, subtle glow, bold white typography.
 */
@Composable
fun AuraSakuraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(AuraPillShape)
            .background(
                if (enabled) AuraSakuraPillGradient else Brush.linearGradient(listOf(AuraCardDark, AuraCardDark))
            )
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            content()
        }
    }
}

/**
 * Outline Pill Button (.btn-outline-pill)
 * Features subtle translucent surface, thin border, pill shape, elegant hover/pressed state.
 */
@Composable
fun AuraOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = AuraBorderDark,
    contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 11.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(AuraPillShape)
            .background(Color(0x0DFFFFFF))
            .border(0.8.dp, borderColor, AuraPillShape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/**
 * White Pill Button (.btn-pill-white)
 * High-contrast luxury white pill button with dark text.
 */
@Composable
fun AuraWhiteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 11.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(AuraPillShape)
            .background(if (enabled) Color.White else Color(0x33FFFFFF))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/**
 * Luxury Glass Card (.luxury-glass-card)
 * Dark translucent surface with thin border and 16dp rounded corners.
 */
@Composable
fun AuraLuxuryGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = AuraCardShape,
    borderColor: Color = AuraBorderDark,
    backgroundColor: Color = AuraCardDark,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(0.8.dp, borderColor, shape)
            .clickable(onClick = onClick)
    } else {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(0.8.dp, borderColor, shape)
    }

    Box(modifier = cardModifier) {
        content()
    }
}

/**
 * Web Editorial Section Header
 * Displays uppercase category tracking badge with sakura dash and Serif title.
 */
@Composable
fun AuraSectionHeader(
    tag: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .background(AuraSakuraPrimary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = tag.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.6.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = AuraSakura400
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = AuraSerifFontFamily
            ),
            color = AuraTextPrimary
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AuraTextSecondary
            )
        }
    }
}
