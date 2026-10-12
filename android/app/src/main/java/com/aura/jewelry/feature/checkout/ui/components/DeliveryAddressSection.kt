package com.aura.jewelry.feature.checkout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.aura.jewelry.feature.checkout.CheckoutUiIntent
import com.aura.jewelry.feature.checkout.CheckoutUiState

@Composable
fun DeliveryAddressSection(
    uiState: CheckoutUiState,
    onIntent: (CheckoutUiIntent) -> Unit,
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
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = AuraGold400
                )
                Text(
                    text = "ĐỊA CHỈ NHẬN TRANG SỨC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.2.sp
                )
            }

            Text(
                text = "Giao hàng bảo mật hỏa tốc có ký nhận tận tay",
                style = MaterialTheme.typography.bodySmall,
                color = AuraMutedText,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Họ và tên người nhận
            LuxuryTextField(
                value = uiState.receiverName,
                onValueChange = { onIntent(CheckoutUiIntent.UpdateReceiverName(it)) },
                label = "Họ và tên người nhận *",
                leadingIcon = Icons.Default.Person,
                isError = uiState.nameError != null,
                errorMessage = uiState.nameError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Số điện thoại
            LuxuryTextField(
                value = uiState.phone,
                onValueChange = { onIntent(CheckoutUiIntent.UpdatePhone(it)) },
                label = "Số điện thoại liên hệ *",
                leadingIcon = Icons.Default.Phone,
                isError = uiState.phoneError != null,
                errorMessage = uiState.phoneError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Địa chỉ chi tiết
            LuxuryTextField(
                value = uiState.streetAddress,
                onValueChange = { onIntent(CheckoutUiIntent.UpdateStreetAddress(it)) },
                label = "Địa chỉ chi tiết (Số nhà, tên đường/tòa nhà) *",
                leadingIcon = Icons.Default.Home,
                isError = uiState.addressError != null,
                errorMessage = uiState.addressError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phường / Xã & Quận / Huyện
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LuxuryTextField(
                    value = uiState.ward,
                    onValueChange = { onIntent(CheckoutUiIntent.UpdateWard(it)) },
                    label = "Phường / Xã",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                LuxuryTextField(
                    value = uiState.district,
                    onValueChange = { onIntent(CheckoutUiIntent.UpdateDistrict(it)) },
                    label = "Quận / Huyện",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tỉnh / Thành phố
            LuxuryTextField(
                value = uiState.province,
                onValueChange = { onIntent(CheckoutUiIntent.UpdateProvince(it)) },
                label = "Tỉnh / Thành phố *",
                leadingIcon = Icons.Default.LocationCity,
                isError = uiState.provinceError != null,
                errorMessage = uiState.provinceError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ghi chú cho thợ kim hoàn & đóng gói
            LuxuryTextField(
                value = uiState.customerNote,
                onValueChange = { onIntent(CheckoutUiIntent.UpdateCustomerNote(it)) },
                label = "Ghi chú đặc biệt (Hộp quà nhung, khắc tên, hẹn giờ...)",
                leadingIcon = Icons.AutoMirrored.Filled.Notes,
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
    }
}

@Composable
fun LuxuryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else AuraGold400
                    )
                }
            },
            singleLine = singleLine,
            maxLines = maxLines,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AuraCardDark,
                unfocusedContainerColor = Color(0xFF0E1017),
                focusedBorderColor = AuraSakuraPrimary,
                unfocusedBorderColor = AuraBorderSubtle,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = AuraSakuraPrimary,
                unfocusedLabelColor = AuraMutedText,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}
