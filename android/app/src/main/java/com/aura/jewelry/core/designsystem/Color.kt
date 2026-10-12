package com.aura.jewelry.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================================
// AURA LUXURY BRAND COLORS - SYNCHRONIZED WITH WEB DESIGN SYSTEM
// Cinematic Ultra-Dark & Luminous Sakura Luxury Aesthetics
// ============================================================================

// Ultra-Dark Backgrounds (Web: --color-bg-pure, --color-bg-deep, --color-bg-surface, --color-bg-card)
val AuraBackgroundPure = Color(0xFF050508)
val AuraBackgroundDark = AuraBackgroundPure
val AuraBackgroundDeep = Color(0xFF090A0F)
val AuraSurfaceDark = Color(0xFF0E1017)
val AuraCardDark = Color(0xFF12141C)
val AuraCardHoverDark = Color(0xFF181B26)
val AuraSurfaceVariantDark = AuraCardDark

// Borders & Dividers (Web: --color-border, --color-border-hover, --color-border-gold, --color-border-sakura)
val AuraBorderDark = Color(0x14FFFFFF) // rgba(255, 255, 255, 0.08)
val AuraBorderSubtle = AuraBorderDark
val AuraBorderHover = Color(0x33FFFFFF) // rgba(255, 255, 255, 0.20)
val AuraBorderGold = Color(0x4DD4AF37) // rgba(212, 175, 55, 0.30)
val AuraBorderSakura = Color(0x4DFF6584) // rgba(255, 101, 132, 0.30)

// Rose & Sakura Luminous Accents (Web: --color-sakura-300, 400, 500, glow)
val AuraSakura300 = Color(0xFFFFD1DC)
val AuraSakura400 = Color(0xFFFF9BB2)
val AuraSakura500 = Color(0xFFFF6584)
val AuraSakuraPrimary = AuraSakura500
val AuraSakuraLight = AuraSakura400
val AuraSakuraDark = Color(0xFFD43F66)
val AuraSakuraGlow = Color(0x59FF6584) // rgba(255, 101, 132, 0.35)
val AuraSakuraContainer = Color(0x26FF6584) // 15% Sakura

// Metallic Gold Palette (Web: --color-gold-300, 400, 500, 600, glow)
val AuraGold300 = Color(0xFFF3E5AB)
val AuraGold400 = Color(0xFFE6CA65)
val AuraGold500 = Color(0xFFD4AF37)
val AuraGold600 = Color(0xFFB89025)
val AuraGoldPrimary = AuraGold500
val AuraGoldLight = AuraGold300
val AuraGoldDark = AuraGold600
val AuraGoldGlow = Color(0x40D4AF37) // rgba(212, 175, 55, 0.25)
val AuraGoldContainer = Color(0x26D4AF37) // 15% Gold

// Typography Colors (Web: --color-text-primary, secondary, muted, gold, sakura)
val AuraTextPrimary = Color(0xFFFFFFFF)
val AuraTextSecondary = Color(0xFF9EA3B0)
val AuraTextMuted = Color(0xFF5E6473)
val AuraTextInverse = Color(0xFF050508)
val AuraTextGold = AuraGold400
val AuraTextSakura = AuraSakura400

// Status & Feedback (Web: --color-success, --color-error, --color-warning)
val AuraSuccessGreen = Color(0xFF10B981)
val AuraSuccessContainer = Color(0x2610B981)
val AuraErrorRed = Color(0xFFEF4444)
val AuraErrorContainer = Color(0x26EF4444)
val AuraWarningAmber = Color(0xFFF59E0B)

// Luxury Gradients (Web: .sakura-gradient-text, .gold-gradient-text, .btn-pill-sakura)
val AuraSakuraPillGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFF6584), Color(0xFFD43F66))
)

val AuraGoldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFF6D6), Color(0xFFD4AF37), Color(0xFFAA7C11))
)

val AuraSakuraGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFB6C1), Color(0xFFFF6584))
)

val AuraDarkCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF161924), Color(0xFF10121A))
)

val AuraGlassCardBg = Color(0xA612141C) // rgba(18, 20, 28, 0.65)

// Convenient design aliases
val AuraDarkBackground = AuraBackgroundDark
val AuraDarkSurface = AuraSurfaceDark
val AuraDarkSurfaceVariant = AuraSurfaceVariantDark
val AuraMutedText = AuraTextMuted
val AuraSakura = AuraSakuraPrimary
val AuraError = AuraErrorRed


