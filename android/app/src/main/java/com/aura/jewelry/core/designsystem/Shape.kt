package com.aura.jewelry.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================================================
// AURA LUXURY SHAPE TOKENS - SYNCHRONIZED WITH WEB DESIGN SYSTEM
// Web: --radius-sm: 6px, --radius-md: 12px, --radius-lg: 20px, --radius-full: 9999px
// ============================================================================

val AuraPillShape = RoundedCornerShape(999.dp)
val AuraCardShape = RoundedCornerShape(16.dp)
val AuraImageShape = RoundedCornerShape(12.dp)
val AuraBadgeShape = RoundedCornerShape(6.dp)

val AuraShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),  // web --radius-sm
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),     // web --radius-md
    large = RoundedCornerShape(16.dp),      // web glass card
    extraLarge = RoundedCornerShape(20.dp)  // web --radius-lg
)

