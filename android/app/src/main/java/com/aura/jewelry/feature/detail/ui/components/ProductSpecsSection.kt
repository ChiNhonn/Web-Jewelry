package com.aura.jewelry.feature.detail.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.core.network.model.ProductDetailDto
import com.aura.jewelry.core.network.model.ProductVariantDto

@Composable
fun ProductSpecsSection(
    product: ProductDetailDto,
    selectedVariant: ProductVariantDto?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Description
        if (!product.description.isNullOrBlank()) {
            Text(
                text = "MÔ TẢ TUYỆT TÁC",
                style = MaterialTheme.typography.labelSmall,
                color = AuraGoldPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = AuraTextSecondary,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. Specifications Table Card
        Text(
            text = "THÔNG SỐ CHẾ TÁC",
            style = MaterialTheme.typography.labelSmall,
            color = AuraGoldPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, AuraBorderDark, com.aura.jewelry.core.designsystem.AuraCardShape),
            shape = com.aura.jewelry.core.designsystem.AuraCardShape,
            color = com.aura.jewelry.core.designsystem.AuraCardDark
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                SpecRow(title = "Mã sản phẩm (SKU)", value = selectedVariant?.sku ?: product.baseSku ?: "AURA-JWL")
                HorizontalDivider(color = AuraBorderDark, modifier = Modifier.padding(vertical = 8.dp))
                SpecRow(title = "Bộ sưu tập", value = product.brand ?: "AURA Haute Joaillerie")
                HorizontalDivider(color = AuraBorderDark, modifier = Modifier.padding(vertical = 8.dp))
                SpecRow(title = "Chất liệu vàng", value = selectedVariant?.materialType ?: product.materialOverview ?: "Vàng 18K Chuẩn Quốc Tế")
                HorizontalDivider(color = AuraBorderDark, modifier = Modifier.padding(vertical = 8.dp))
                SpecRow(title = "Kích thước (Size/Ni)", value = selectedVariant?.sizeOption ?: "Tiêu chuẩn (Freesize)")
                HorizontalDivider(color = AuraBorderDark, modifier = Modifier.padding(vertical = 8.dp))
                SpecRow(title = "Danh mục", value = product.category?.name ?: "Trang sức cao cấp")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. GIA Diamond Certification Assurance Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, AuraBorderDark, com.aura.jewelry.core.designsystem.AuraCardShape),
            shape = com.aura.jewelry.core.designsystem.AuraCardShape,
            color = com.aura.jewelry.core.designsystem.AuraCardDark
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                DetailFeatureCard(
                    icon = Icons.Default.VerifiedUser,
                    title = "Kiểm Định Kim Cương GIA Độc Lập",
                    description = "Mỗi viên kim cương chủ đều sở hữu mã số cạnh laser trùng khớp với chứng thư kiểm định GIA quốc tế."
                )
                Spacer(modifier = Modifier.height(14.dp))
                DetailFeatureCard(
                    icon = Icons.Default.Diamond,
                    title = "Bảo Hành & Làm Mới Trọn Đời",
                    description = "Đặc quyền bảo dưỡng, gắn đá phụ và đánh bóng siêu âm miễn phí vĩnh viễn tại xưởng AURA."
                )
                Spacer(modifier = Modifier.height(14.dp))
                DetailFeatureCard(
                    icon = Icons.Default.CleaningServices,
                    title = "Hướng Dẫn Bảo Quản",
                    description = "Tránh để trang sức tiếp xúc trực tiếp hóa chất mỹ phẩm. Vui lòng bảo quản trong hộp nhung chuyên dụng."
                )
            }
        }
    }
}

@Composable
private fun SpecRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = AuraTextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = AuraTextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailFeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AuraGoldPrimary,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = AuraTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = AuraTextSecondary
            )
        }
    }
}
