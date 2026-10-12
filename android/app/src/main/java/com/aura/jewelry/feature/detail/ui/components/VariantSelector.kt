package com.aura.jewelry.feature.detail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraErrorRed
import com.aura.jewelry.core.designsystem.AuraGoldContainer
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSuccessGreen
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.core.network.model.ProductVariantDto

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VariantSelector(
    variants: List<ProductVariantDto>,
    selectedVariant: ProductVariantDto?,
    quantity: Int,
    onVariantSelected: (ProductVariantDto) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (variants.isEmpty()) return

    // Group variants by Material Type and Size
    val materials = variants.mapNotNull { it.materialType }.distinct()
    val sizes = variants.mapNotNull { it.sizeOption }.distinct()

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Material Selector (If applicable)
        if (materials.isNotEmpty()) {
            Text(
                text = "CHẤT LIỆU CHẾ TÁC",
                style = MaterialTheme.typography.labelSmall,
                color = AuraGoldPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                materials.forEach { material ->
                    val isSelected = selectedVariant?.materialType == material
                    // Find variant matching this material (and current size if possible)
                    val matchingVariant = variants.find {
                        it.materialType == material &&
                                (selectedVariant?.sizeOption == null || it.sizeOption == selectedVariant.sizeOption)
                    } ?: variants.firstOrNull { it.materialType == material }

                    val isAvailable = matchingVariant?.let { it.availableQuantity > 0 } ?: false

                    ChipSelectorItem(
                        label = material,
                        isSelected = isSelected,
                        isEnabled = isAvailable,
                        colorDot = getMaterialColor(material),
                        onClick = {
                            if (matchingVariant != null) onVariantSelected(matchingVariant)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 2. Size Selector (If applicable)
        if (sizes.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KÍCH THƯỚC (SIZE / NI)",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraGoldPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hướng dẫn đo size",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuraTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sizes.forEach { size ->
                    val isSelected = selectedVariant?.sizeOption == size
                    val matchingVariant = variants.find {
                        it.sizeOption == size &&
                                (selectedVariant?.materialType == null || it.materialType == selectedVariant.materialType)
                    } ?: variants.firstOrNull { it.sizeOption == size }

                    val isAvailable = matchingVariant?.let { it.availableQuantity > 0 } ?: false

                    ChipSelectorItem(
                        label = size,
                        isSelected = isSelected,
                        isEnabled = isAvailable,
                        onClick = {
                            if (matchingVariant != null) onVariantSelected(matchingVariant)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. Stock Status & Quantity Stepper Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stock Indicator
            val stock = selectedVariant?.availableQuantity ?: 0
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (stock > 0) AuraSuccessGreen else AuraErrorRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (stock > 5) "Còn hàng tại xưởng"
                    else if (stock > 0) "Chỉ còn $stock món sẵn có"
                    else "Tạm hết hàng",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (stock > 0) AuraTextPrimary else AuraErrorRed
                )
            }

            // Quantity Stepper (matches web line 490: pill shape, rgba(255,255,255,0.05), border)
            if (stock > 0) {
                Surface(
                    color = Color(0x0DFFFFFF),
                    shape = com.aura.jewelry.core.designsystem.AuraPillShape,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, AuraBorderDark)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { onQuantityChanged(quantity - 1) },
                            enabled = quantity > 1,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Giảm",
                                tint = if (quantity > 1) AuraTextPrimary else AuraTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AuraTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        IconButton(
                            onClick = { onQuantityChanged(quantity + 1) },
                            enabled = quantity < stock,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = if (quantity < stock) AuraTextPrimary else AuraTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipSelectorItem(
    label: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    colorDot: Color? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(com.aura.jewelry.core.designsystem.AuraPillShape)
            .background(
                when {
                    isSelected -> AuraGoldContainer
                    !isEnabled -> Color(0xFF0A0C12)
                    else -> Color(0x0DFFFFFF)
                }
            )
            .border(
                width = if (isSelected) 1.dp else 0.8.dp,
                color = when {
                    isSelected -> AuraGoldPrimary
                    !isEnabled -> AuraBorderDark.copy(alpha = 0.3f)
                    else -> AuraBorderDark
                },
                shape = com.aura.jewelry.core.designsystem.AuraPillShape
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (colorDot != null) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colorDot)
                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = when {
                    isSelected -> AuraGoldPrimary
                    !isEnabled -> AuraTextMuted.copy(alpha = 0.5f)
                    else -> AuraTextSecondary
                }
            )
        }
    }
}

private fun getMaterialColor(material: String): Color {
    val lower = material.lowercase()
    return when {
        lower.contains("trắng") || lower.contains("white") || lower.contains("platinum") -> Color(0xFFE5E7EB)
        lower.contains("hồng") || lower.contains("rose") -> Color(0xFFF4A896)
        lower.contains("vàng") || lower.contains("yellow") || lower.contains("gold") -> Color(0xFFE6C65A)
        else -> Color(0xFFD4AF37)
    }
}
