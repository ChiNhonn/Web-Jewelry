package com.aura.jewelry.feature.checkout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraMutedText
import com.aura.jewelry.core.designsystem.AuraOutlineButton
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.designsystem.AuraSakuraButton
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.network.model.OrderResponseDto

@Composable
fun OrderSuccessDialog(
    order: OrderResponseDto,
    onViewOrderDetails: (String) -> Unit,
    onContinueShopping: () -> Unit
) {
    Dialog(
        onDismissRequest = { /* Prevent accidental dismissal, force user choice */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = AuraCardShape,
            colors = CardDefaults.cardColors(containerColor = AuraCardDark),
            border = BorderStroke(1.dp, AuraBorderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sparkling Luxury Icon Container
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0E1017))
                        .border(1.2.dp, AuraSakuraPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = AuraGold400,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ĐẶT HÀNG THÀNH CÔNG!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AuraGold400,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cảm ơn quý khách đã tin tưởng lựa chọn tuyệt tác trang sức thủ công từ AURA Fine Jewelry.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Order Code Chip
                Card(
                    shape = AuraPillShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1017)),
                    border = BorderStroke(0.8.dp, AuraBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Mã đơn hàng:",
                            style = MaterialTheme.typography.bodySmall,
                            color = AuraMutedText
                        )
                        Text(
                            text = order.orderCode,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AuraGold400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Chuyên viên chăm sóc khách hàng VIP sẽ liên hệ xác nhận đơn hàng và lịch giao bảo hiểm trong vòng 15 phút.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuraMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary CTA: Xem chi tiết đơn
                AuraSakuraButton(
                    onClick = { onViewOrderDetails(order.orderCode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "XEM CHI TIẾT ĐƠN HÀNG",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary CTA: Tiếp tục mua sắm
                AuraOutlineButton(
                    onClick = onContinueShopping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "TIẾP TỤC MUA SẮM",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
