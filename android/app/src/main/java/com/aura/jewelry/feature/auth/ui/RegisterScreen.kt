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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
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
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Luxury Brand Emblem
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AuraCardDark)
                .border(1.2.dp, AuraSakuraPrimary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = null,
                tint = AuraGold400,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "GIA NHẬP AURA VIP",
            style = MaterialTheme.typography.headlineLarge,
            color = AuraGold400,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Đăng ký thành viên để nhận ưu đãi thượng lưu & tích điểm kim cương",
            style = MaterialTheme.typography.bodyMedium,
            color = AuraTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

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

        // Full Name (First + Last)
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = uiState.registerLastName,
                onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateRegisterLastName(it)) },
                modifier = Modifier.weight(1f),
                label = { Text("Họ & Đệm") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                colors = textFieldColors(),
                shape = AuraPillShape
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = uiState.registerFirstName,
                onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateRegisterFirstName(it)) },
                modifier = Modifier.weight(1f),
                label = { Text("Tên") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = textFieldColors(),
                shape = AuraPillShape
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Username
        OutlinedTextField(
            value = uiState.registerUsername,
            onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateRegisterUsername(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tên tài khoản (Username) *") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AuraGold400) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = textFieldColors(),
            shape = AuraPillShape
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Email
        OutlinedTextField(
            value = uiState.registerEmail,
            onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateRegisterEmail(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email liên hệ *") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AuraGold400) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = textFieldColors(),
            shape = AuraPillShape
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Phone Number
        OutlinedTextField(
            value = uiState.registerPhone,
            onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateRegisterPhone(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Số điện thoại nhận hàng") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AuraGold400) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = textFieldColors(),
            shape = AuraPillShape
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password
        OutlinedTextField(
            value = uiState.registerPassword,
            onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateRegisterPassword(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mật khẩu (tối thiểu 6 ký tự) *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AuraGold400) },
            trailingIcon = {
                IconButton(onClick = { viewModel.onIntent(AuthUiIntent.ToggleRegisterPasswordVisibility) }) {
                    Icon(
                        imageVector = if (uiState.isRegisterPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = AuraTextMuted
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (uiState.isRegisterPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = textFieldColors(),
            shape = AuraPillShape
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Confirm Password
        OutlinedTextField(
            value = uiState.registerConfirmPassword,
            onValueChange = { viewModel.onIntent(AuthUiIntent.UpdateRegisterConfirmPassword(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Xác nhận mật khẩu *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AuraGold400) },
            trailingIcon = {
                IconButton(onClick = { viewModel.onIntent(AuthUiIntent.ToggleRegisterConfirmPasswordVisibility) }) {
                    Icon(
                        imageVector = if (uiState.isRegisterConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = AuraTextMuted
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (uiState.isRegisterConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                viewModel.onIntent(AuthUiIntent.SubmitRegister)
            }),
            colors = textFieldColors(),
            shape = AuraPillShape
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button: Luxury Sakura Pill Button
        AuraSakuraButton(
            onClick = {
                focusManager.clearFocus()
                viewModel.onIntent(AuthUiIntent.SubmitRegister)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !uiState.isLoading,
            isLoading = uiState.isLoading
        ) {
            Text(
                text = "TẠO TÀI KHOẢN VIP",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to Login
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đã có tài khoản?",
                style = MaterialTheme.typography.bodyMedium,
                color = AuraTextSecondary
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Đăng nhập ngay",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuraGold400,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AuraSakuraPrimary,
    unfocusedBorderColor = AuraBorderSubtle,
    focusedLabelColor = AuraSakuraPrimary,
    unfocusedLabelColor = AuraTextMuted,
    focusedTextColor = AuraTextPrimary,
    unfocusedTextColor = AuraTextPrimary,
    focusedContainerColor = AuraCardDark,
    unfocusedContainerColor = AuraCardDark
)
