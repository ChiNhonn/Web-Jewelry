package com.aura.jewelry.feature.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextInverse
import com.aura.jewelry.core.designsystem.AuraTextSecondary

@Composable
fun EmptyCartView(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraBackgroundDark)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Luxury Bag Emblem
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(AuraSurfaceDark)
                .border(1.dp, AuraBorderDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = AuraGoldPrimary,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TÚI ĐỒ ĐANG TRỐNG",
            style = MaterialTheme.typography.headlineMedium,
            color = AuraGoldPrimary,
            letterSpacing = MaterialTheme.typography.headlineMedium.letterSpacing
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Quý khách chưa chọn món trang sức nào. Hãy khám phá bộ sưu tập kim cương tinh xảo và chọn cho mình kiệt tác ưng ý nhất.",
            style = MaterialTheme.typography.bodyMedium,
            color = AuraTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onExploreClick,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = AuraGoldPrimary,
                contentColor = AuraTextInverse
            )
        ) {
            Text(
                text = "KHÁM PHÁ BỘ SƯU TẬP",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
