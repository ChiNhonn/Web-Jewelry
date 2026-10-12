package com.aura.jewelry.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.jewelry.core.designsystem.AuraBackgroundPure
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.core.network.model.CategoryDto

/**
 * Category Chips Row matching Web tab pills:
 * - Active: Pure White pill with Dark Pure text
 * - Inactive: Translucent glass pill with subtle border and muted text
 */
@Composable
fun CategoryChipsRow(
    categories: List<CategoryDto>,
    selectedCategorySlug: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" Chip
        item {
            val isSelected = selectedCategorySlug == null
            CategoryChipItem(
                title = "Tất Cả",
                isSelected = isSelected,
                onClick = { onCategorySelected(null) }
            )
        }

        // Dynamic categories from Backend
        items(categories, key = { it.id }) { category ->
            val isSelected = selectedCategorySlug == category.slug
            CategoryChipItem(
                title = category.name,
                isSelected = isSelected,
                onClick = { onCategorySelected(category.slug) }
            )
        }
    }
}

@Composable
private fun CategoryChipItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(AuraPillShape)
            .background(if (isSelected) Color.White else Color(0x0DFFFFFF))
            .border(
                width = 0.8.dp,
                color = if (isSelected) Color.White else AuraBorderDark,
                shape = AuraPillShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isSelected) AuraBackgroundPure else AuraTextSecondary
        )
    }
}

