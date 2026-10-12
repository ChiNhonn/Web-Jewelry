package com.aura.jewelry.feature.checkout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraDarkSurface
import com.aura.jewelry.core.designsystem.AuraDarkSurfaceVariant
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraMutedText
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.feature.checkout.PaymentMethod

@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onSelectMethod: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AuraCardShape,
        colors = CardDefaults.cardColors(containerColor = AuraCardDark),
        border = BorderStroke(0.8.dp, AuraBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = AuraGold400
                )
                Text(
                    text = "PHƯƠNG THỨC THANH TOÁN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.2.sp
                )
            }

            Text(
                text = "Hỗ trợ thanh toán bảo mật chuẩn mã hóa ngân hàng",
                style = MaterialTheme.typography.bodySmall,
                color = AuraMutedText,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PaymentMethodItem(
                    method = PaymentMethod.COD,
                    icon = Icons.Default.LocalAtm,
                    isSelected = selectedMethod == PaymentMethod.COD,
                    onClick = { onSelectMethod(PaymentMethod.COD) }
                )

                PaymentMethodItem(
                    method = PaymentMethod.BANK_TRANSFER,
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedMethod == PaymentMethod.BANK_TRANSFER,
                    onClick = { onSelectMethod(PaymentMethod.BANK_TRANSFER) }
                )

                PaymentMethodItem(
                    method = PaymentMethod.VNPAY,
                    icon = Icons.Default.QrCode,
                    isSelected = selectedMethod == PaymentMethod.VNPAY,
                    onClick = { onSelectMethod(PaymentMethod.VNPAY) }
                )

                PaymentMethodItem(
                    method = PaymentMethod.MOMO,
                    icon = Icons.Default.Payment,
                    isSelected = selectedMethod == PaymentMethod.MOMO,
                    onClick = { onSelectMethod(PaymentMethod.MOMO) }
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: PaymentMethod,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) AuraSakuraPrimary else AuraBorderSubtle
    val bgColor = if (isSelected) AuraSakuraPrimary.copy(alpha = 0.08f) else Color(0xFF0E1017)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isSelected) 1.2.dp else 0.8.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = AuraSakuraPrimary,
                    unselectedColor = Color.White.copy(alpha = 0.35f)
                ),
                modifier = Modifier.size(20.dp)
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AuraSakuraPrimary else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = method.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraMutedText,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
