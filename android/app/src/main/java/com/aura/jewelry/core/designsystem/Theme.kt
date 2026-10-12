package com.aura.jewelry.core.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================================================
// AURA LUXURY MATERIAL 3 COLOR SCHEME
// ============================================================================

val AuraDarkColorScheme = darkColorScheme(
    primary = AuraGoldPrimary,
    onPrimary = AuraTextInverse,
    primaryContainer = AuraGoldContainer,
    onPrimaryContainer = AuraGoldLight,
    secondary = AuraSakuraPrimary,
    onSecondary = AuraTextInverse,
    secondaryContainer = AuraSakuraContainer,
    onSecondaryContainer = AuraSakuraLight,
    tertiary = AuraGoldLight,
    onTertiary = AuraTextInverse,
    background = AuraBackgroundDark,
    onBackground = AuraTextPrimary,
    surface = AuraSurfaceDark,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraSurfaceVariantDark,
    onSurfaceVariant = AuraTextSecondary,
    outline = AuraBorderDark,
    outlineVariant = AuraBorderDark,
    error = AuraErrorRed,
    onError = AuraTextPrimary,
    errorContainer = AuraErrorContainer,
    onErrorContainer = AuraErrorRed
)

/**
 * Main Theme Composable for AURA Jewelry Android App.
 * Enforces Dark Luxury Aesthetic with Gold & Sakura accents across all components.
 */
@Composable
fun AuraJewelryTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = AuraDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Set system bars color matching Aura Dark Background
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()

                // Light icons for dark background
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AuraTypography,
        shapes = AuraShapes,
        content = content
    )
}
