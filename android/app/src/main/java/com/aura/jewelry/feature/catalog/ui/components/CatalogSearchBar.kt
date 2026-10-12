package com.aura.jewelry.feature.catalog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary

@Composable
fun CatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    activeFilterCount: Int,
    onOpenFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Input Box (pill shape matching web search inputs)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(com.aura.jewelry.core.designsystem.AuraPillShape)
                .background(Color(0x0DFFFFFF))
                .border(0.8.dp, AuraBorderDark, com.aura.jewelry.core.designsystem.AuraPillShape)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = com.aura.jewelry.core.designsystem.AuraGold400,
                    modifier = Modifier.size(18.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Tìm kiếm trang sức, kim cương...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuraTextMuted,
                            fontSize = 13.sp
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        cursorBrush = SolidColor(AuraGoldPrimary),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = AuraTextPrimary,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Xóa từ khóa",
                            tint = AuraTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Filter Button with Badge (circular frosted glass matching web)
        IconButton(
            onClick = onOpenFilterClick,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0x0DFFFFFF))
                .border(
                    width = if (activeFilterCount > 0) 1.dp else 0.8.dp,
                    color = if (activeFilterCount > 0) com.aura.jewelry.core.designsystem.AuraSakuraPrimary else AuraBorderDark,
                    shape = CircleShape
                )
        ) {
            BadgedBox(
                badge = {
                    if (activeFilterCount > 0) {
                        Badge(
                            containerColor = com.aura.jewelry.core.designsystem.AuraSakuraPrimary,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = activeFilterCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Bộ lọc",
                    tint = if (activeFilterCount > 0) com.aura.jewelry.core.designsystem.AuraSakuraPrimary else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
