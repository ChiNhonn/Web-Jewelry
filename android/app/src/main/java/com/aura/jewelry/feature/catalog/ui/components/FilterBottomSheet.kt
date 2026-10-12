package com.aura.jewelry.feature.catalog.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraSurfaceVariantDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.feature.catalog.CatalogUiIntent
import com.aura.jewelry.feature.catalog.CatalogUiState
import com.aura.jewelry.feature.catalog.PRICE_PRESETS
import com.aura.jewelry.feature.catalog.SortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    uiState: CatalogUiState,
    onIntent: (CatalogUiIntent) -> Unit,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = AuraSurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BỘ LỌC TÌM KIẾM",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AuraTextPrimary,
                    letterSpacing = 1.2.sp
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = AuraTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 1: Sắp xếp
            FilterSectionTitle(title = "SẮP XẾP THEO")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOption.entries.forEach { sort ->
                    val isSelected = uiState.selectedSort == sort
                    LuxuryFilterChip(
                        selected = isSelected,
                        label = sort.title,
                        onClick = { onIntent(CatalogUiIntent.SelectSort(sort)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = AuraBorderDark)
            Spacer(modifier = Modifier.height(18.dp))

            // Section 2: Danh mục
            FilterSectionTitle(title = "DANH MỤC TRANG SỨC")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Option: Tất cả
                LuxuryFilterChip(
                    selected = uiState.selectedCategorySlug == null,
                    label = "Tất cả danh mục",
                    onClick = { onIntent(CatalogUiIntent.SelectCategory(null)) }
                )

                uiState.categories.forEach { category ->
                    val isSelected = uiState.selectedCategorySlug == category.slug
                    LuxuryFilterChip(
                        selected = isSelected,
                        label = category.name,
                        onClick = { onIntent(CatalogUiIntent.SelectCategory(category.slug)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = AuraBorderDark)
            Spacer(modifier = Modifier.height(18.dp))

            // Section 3: Mức giá
            FilterSectionTitle(title = "KHOẢNG GIÁ")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PRICE_PRESETS.forEach { preset ->
                    val isSelected = uiState.selectedPricePreset == preset
                    LuxuryFilterChip(
                        selected = isSelected,
                        label = preset.title,
                        onClick = { onIntent(CatalogUiIntent.SelectPricePreset(preset)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = AuraBorderDark)
            Spacer(modifier = Modifier.height(18.dp))

            // Section 4: Chỉ hiển thị nổi bật
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = AuraGoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Bộ sưu tập Nổi bật (Featured)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AuraTextPrimary
                        )
                        Text(
                            text = "Chỉ hiển thị các tuyệt tác chế tác giới hạn",
                            style = MaterialTheme.typography.labelSmall,
                            color = AuraTextMuted
                        )
                    }
                }

                Switch(
                    checked = uiState.isFeaturedOnly,
                    onCheckedChange = { onIntent(CatalogUiIntent.ToggleFeaturedOnly(it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AuraGoldPrimary,
                        checkedTrackColor = AuraGoldPrimary.copy(alpha = 0.3f),
                        uncheckedThumbColor = AuraTextMuted,
                        uncheckedTrackColor = AuraSurfaceVariantDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions: Reset & Apply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onIntent(CatalogUiIntent.ResetFilters) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraTextPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "ĐẶT LẠI",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuraGoldPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "ÁP DỤNG",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = AuraGoldPrimary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun LuxuryFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AuraGoldPrimary.copy(alpha = 0.2f),
            selectedLabelColor = AuraGoldPrimary,
            containerColor = AuraSurfaceVariantDark.copy(alpha = 0.6f),
            labelColor = AuraTextMuted
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color.White.copy(alpha = 0.1f),
            selectedBorderColor = AuraGoldPrimary
        ),
        shape = RoundedCornerShape(10.dp)
    )
}
