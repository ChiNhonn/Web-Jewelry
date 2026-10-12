package com.aura.jewelry.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraCardShape
import com.aura.jewelry.core.designsystem.AuraErrorRed
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldContainer
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraOutlineButton
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.designsystem.AuraSakuraButton
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextInverse
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.feature.auth.AuthUiIntent
import com.aura.jewelry.feature.auth.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onIntent(AuthUiIntent.RefreshProfile)
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Xác nhận đăng xuất",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuraTextPrimary
                )
            },
            text = {
                Text(
                    text = "Quý khách có chắc chắn muốn đăng xuất khỏi tài khoản AURA trên thiết bị này không?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuraTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.onIntent(AuthUiIntent.SubmitLogout)
                    }
                ) {
                    Text(text = "Đăng Xuất", color = AuraErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Hủy", color = AuraTextMuted)
                }
            },
            containerColor = AuraSurfaceDark,
            shape = MaterialTheme.shapes.large
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraBackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoggedIn) {
            // ================================================================
            // LOGGED-IN VIP MEMBER VIEW
            // ================================================================
            Spacer(modifier = Modifier.height(12.dp))

            // VIP Avatar Monogram
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AuraCardDark)
                    .border(1.5.dp, AuraSakuraPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.user?.username?.take(2)?.uppercase() ?: "AU",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AuraGold400,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = uiState.user?.fullName ?: uiState.user?.username ?: "Thành Viên AURA",
                style = MaterialTheme.typography.headlineSmall,
                color = AuraTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = uiState.user?.email.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = AuraTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // VIP Badge
            Surface(
                color = AuraGoldContainer,
                shape = AuraPillShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraGold400.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = AuraGold400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VIP DIAMOND CLIENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = AuraGold400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section: Orders & Shopping
            ProfileMenuSection(title = "ĐƠN HÀNG & MUA SẮM") {
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    title = "Lịch Sử Đơn Hàng",
                    subtitle = "Xem trạng thái và chi tiết các đơn đã đặt",
                    onClick = onNavigateToOrders
                )
                ProfileMenuItem(
                    icon = Icons.Default.Favorite,
                    title = "Danh Sách Yêu Thích",
                    subtitle = "Trang sức bạn đã quan tâm lưu trữ",
                    onClick = onNavigateToWishlist
                )
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Sổ Địa Chỉ Nhận Hàng",
                    subtitle = "Địa chỉ giao nhận kim hoàn bảo hiểm"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Privileges & Security
            ProfileMenuSection(title = "ĐẶC QUYỀN & BẢO MẬT") {
                ProfileMenuItem(
                    icon = Icons.Default.CardMembership,
                    title = "Quyền Lợi Hội Viên & GIA",
                    subtitle = "Bảo hiểm sản phẩm, kiểm định và làm mới trọn đời"
                )
                ProfileMenuItem(
                    icon = Icons.Default.Security,
                    title = "Bảo Mật Tài Khoản",
                    subtitle = "Đổi mật khẩu & xác thực 2 lớp"
                )
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Đăng Xuất",
                    subtitle = "Thoát tài khoản an toàn khỏi thiết bị",
                    iconTint = AuraErrorRed,
                    titleColor = AuraErrorRed,
                    onClick = { showLogoutDialog = true }
                )
            }
        } else {
            // ================================================================
            // GUEST VIEW
            // ================================================================
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AuraCardDark)
                    .border(1.2.dp, AuraSakuraPrimary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AuraGold400,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CHÀO MỪNG QUÝ KHÁCH",
                style = MaterialTheme.typography.headlineMedium,
                color = AuraGold400,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Đăng nhập hoặc đăng ký tài khoản AURA để nhận ưu đãi thượng lưu, theo dõi đơn hàng và tận hưởng dịch vụ chăm sóc VIP riêng biệt.",
                style = MaterialTheme.typography.bodyMedium,
                color = AuraTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuraSakuraButton(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "ĐĂNG NHẬP NGAY VIP",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            AuraOutlineButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "TẠO TÀI KHOẢN MỚI",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AuraGold400,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = AuraGold400,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, AuraBorderSubtle, AuraCardShape),
            shape = AuraCardShape,
            color = AuraCardDark
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: androidx.compose.ui.graphics.Color = AuraGoldPrimary,
    titleColor: androidx.compose.ui.graphics.Color = AuraTextPrimary,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AuraTextMuted
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = AuraTextMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
    }
}
