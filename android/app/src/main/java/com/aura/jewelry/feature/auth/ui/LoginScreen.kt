package com.aura.jewelry.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraBorderSubtle
import com.aura.jewelry.core.designsystem.AuraCardDark
import com.aura.jewelry.core.designsystem.AuraErrorContainer
import com.aura.jewelry.core.designsystem.AuraErrorRed
import com.aura.jewelry.core.designsystem.AuraGold400
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraPillShape
import com.aura.jewelry.core.designsystem.AuraSakuraButton
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextInverse
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary
import com.aura.jewelry.core.designsystem.AuraTextSecondary
import com.aura.jewelry.feature.auth.AuthUiEffect
import com.aura.jewelry.feature.auth.AuthUiIntent
import com.aura.jewelry.feature.auth.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                AuthUiEffect.NavigateToHome, AuthUiEffect.NavigateBack -> onNavigateToHome()
                is AuthUiEffect.ShowToast -> {}
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraBackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Luxury Brand Emblem
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(AuraCardDark)
                .border(1.2.dp, AuraSakuraPrimary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = null,
                tint = AuraGold400,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "AURA JEWELRY",
            style = MaterialTheme.typography.displaySmall,
            color = AuraGold400,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Đăng nhập để khám phá tuyệt tác & hưởng đặc quyền VIP",
            style = MaterialTheme.typography.bodyMedium,
            color = AuraTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error Banner
        if (uiState.errorMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AuraErrorRed.copy(alpha = 0.5f), AuraPillShape),
                shape = AuraPillShape,
                color = AuraErrorContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = AuraErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = AuraTextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Username / Email Field
        OutlinedTextField(
            value = uiState.loginUsernameOrEmail,
            onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateLoginUsernameOrEmail(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tên đăng nhập hoặc Email") },
            placeholder = { Text("customer@jewelry.com") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AuraGold400
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraSakuraPrimary,
                unfocusedBorderColor = AuraBorderSubtle,
                focusedLabelColor = AuraSakuraPrimary,
                unfocusedLabelColor = AuraTextMuted,
                focusedTextColor = AuraTextPrimary,
                unfocusedTextColor = AuraTextPrimary,
                focusedContainerColor = AuraCardDark,
                unfocusedContainerColor = AuraCardDark
            ),
            shape = AuraPillShape
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = uiState.loginPassword,
            onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateLoginPassword(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mật khẩu") },
            placeholder = { Text("••••••••") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AuraGold400
                )
            },
            trailingIcon = {
                IconButton(onClick = { viewModel.onIntent(AuthUiIntent.ToggleLoginPasswordVisibility) }) {
                    Icon(
                        imageVector = if (uiState.isLoginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Ẩn/Hiện mật khẩu",
                        tint = AuraTextMuted
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (uiState.isLoginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.onIntent(AuthUiIntent.SubmitLogin)
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraSakuraPrimary,
                unfocusedBorderColor = AuraBorderSubtle,
                focusedLabelColor = AuraSakuraPrimary,
                unfocusedLabelColor = AuraTextMuted,
                focusedTextColor = AuraTextPrimary,
                unfocusedTextColor = AuraTextPrimary,
                focusedContainerColor = AuraCardDark,
                unfocusedContainerColor = AuraCardDark
            ),
            shape = AuraPillShape
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Submit Button: Luxury Sakura Pill Button matching Web .btn-pill-sakura
        AuraSakuraButton(
            onClick = {
                focusManager.clearFocus()
                viewModel.onIntent(AuthUiIntent.SubmitLogin)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !uiState.isLoading,
            isLoading = uiState.isLoading
        ) {
            Text(
                text = "ĐĂNG NHẬP VIP",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Switch to Register
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chưa có tài khoản?",
                style = MaterialTheme.typography.bodyMedium,
                color = AuraTextSecondary
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Đăng ký thành viên VIP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuraGoldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Guest continue
        TextButton(onClick = onNavigateToHome) {
            Text(
                text = "Tiếp tục xem trang sức với tư cách Khách",
                style = MaterialTheme.typography.labelMedium,
                color = AuraTextMuted
            )
        }
    }
}
