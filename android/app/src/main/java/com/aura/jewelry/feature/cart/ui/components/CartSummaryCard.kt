package com.aura.jewelry.feature.cart.ui.components

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldContainer
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSuccessGreen
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextInverse
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.core.network.model.CartResponseDto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartSummaryCard(
    cart: CartResponseDto,
    couponCode: String,
    couponDiscount: Double,
    couponApplied: Boolean,
    couponMessage: String?,
    totalAmount: Double,
    onCouponCodeChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onProceedToCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(0.8.dp, AuraBorderDark, com.aura.jewelry.core.designsystem.AuraCardShape),
        shape = com.aura.jewelry.core.designsystem.AuraCardShape,
        color = com.aura.jewelry.core.designsystem.AuraCardDark
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 1. Coupon Input Row
            Text(
                text = "MÃ ƯU ĐÃI VIP / VOUCHER",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = com.aura.jewelry.core.designsystem.AuraSakura300
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = couponCode,
                    onValueChange = onCouponCodeChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ví dụ: VIPDIAMOND", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Loyalty,
                            contentDescription = null,
                            tint = com.aura.jewelry.core.designsystem.AuraGold400,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onApplyCoupon()
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.aura.jewelry.core.designsystem.AuraGold400,
                        unfocusedBorderColor = AuraBorderDark,
                        focusedTextColor = AuraTextPrimary,
                        unfocusedTextColor = AuraTextPrimary,
                        focusedContainerColor = Color(0x0DFFFFFF),
                        unfocusedContainerColor = Color(0x0DFFFFFF)
                    ),
                    shape = com.aura.jewelry.core.designsystem.AuraPillShape
                )

                Spacer(modifier = Modifier.width(10.dp))

                com.aura.jewelry.core.designsystem.AuraOutlineButton(
                    onClick = {
                        focusManager.clearFocus()
                        onApplyCoupon()
                    },
                    enabled = couponCode.isNotBlank(),
                    borderColor = if (couponCode.isNotBlank()) com.aura.jewelry.core.designsystem.AuraGold400 else AuraBorderDark,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = "Áp dụng",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (couponCode.isNotBlank()) com.aura.jewelry.core.designsystem.AuraGold400 else AuraTextMuted
                    )
                }
            }

            // Coupon Feedback Message
            if (!couponMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (couponApplied) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AuraSuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = couponMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (couponApplied) AuraSuccessGreen else AuraGoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = AuraBorderDark)
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Cost Breakdown
            SummaryRow(title = "Tạm tính (${cart.totalQuantity} món)", value = currencyFormatter.format(cart.subtotal))

            if (couponDiscount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow(
                    title = "Ưu đãi VIP giảm trừ",
                    value = "-${currencyFormatter.format(couponDiscount)}",
                    valueColor = AuraSuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow(
                title = "Vận chuyển bảo hiểm hỏa tốc",
                value = if (cart.shippingFee > 0) currencyFormatter.format(cart.shippingFee) else "Miễn phí toàn quốc",
                valueColor = if (cart.shippingFee <= 0) AuraSuccessGreen else AuraTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = AuraBorderDark)
            Spacer(modifier = Modifier.height(14.dp))

            // 3. Final Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TỔNG THANH TOÁN",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = AuraTextPrimary
                )
                Text(
                    text = currencyFormatter.format(totalAmount),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = com.aura.jewelry.core.designsystem.AuraGold400
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Checkout Button (.btn-pill-sakura matching web)
            com.aura.jewelry.core.designsystem.AuraSakuraButton(
                onClick = onProceedToCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TIẾN HÀNH ĐẶT HÀNG VIP",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String,
    valueColor: Color = AuraTextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodySmall, color = AuraTextMuted)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Medium)
    }
}
