package com.aura.jewelry.feature.auth

import com.aura.jewelry.core.network.model.UserSummaryDto

/**
 * UI State for Authentication features (Login, Register, VIP Profile).
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val user: UserSummaryDto? = null,

    // Login Form State
    val loginUsernameOrEmail: String = "",
    val loginPassword: String = "",
    val isLoginPasswordVisible: Boolean = false,

    // Registration Form State
    val registerUsername: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    val registerFirstName: String = "",
    val registerLastName: String = "",
    val registerPhone: String = "",
    val isRegisterPasswordVisible: Boolean = false,
    val isRegisterConfirmPasswordVisible: Boolean = false
) {
    val isLoginFormValid: Boolean
        get() = loginUsernameOrEmail.isNotBlank() && loginPassword.length >= 6

    val isRegisterFormValid: Boolean
        get() = registerUsername.isNotBlank() &&
                registerEmail.contains("@") &&
                registerPassword.length >= 6 &&
                registerPassword == registerConfirmPassword
}

/**
 * UI Intents (User actions) for Authentication.
 */
sealed interface AuthUiIntent {
    // Login Intents
    data class UpdateLoginUsernameOrEmail(val value: String) : AuthUiIntent
    data class UpdateLoginPassword(val value: String) : AuthUiIntent
    data object ToggleLoginPasswordVisibility : AuthUiIntent
    data object SubmitLogin : AuthUiIntent

    // Register Intents
    data class UpdateRegisterUsername(val value: String) : AuthUiIntent
    data class UpdateRegisterEmail(val value: String) : AuthUiIntent
    data class UpdateRegisterPassword(val value: String) : AuthUiIntent
    data class UpdateRegisterConfirmPassword(val value: String) : AuthUiIntent
    data class UpdateRegisterFirstName(val value: String) : AuthUiIntent
    data class UpdateRegisterLastName(val value: String) : AuthUiIntent
    data class UpdateRegisterPhone(val value: String) : AuthUiIntent
    data object ToggleRegisterPasswordVisibility : AuthUiIntent
    data object ToggleRegisterConfirmPasswordVisibility : AuthUiIntent
    data object SubmitRegister : AuthUiIntent

    // Session Intents
    data object SubmitLogout : AuthUiIntent
    data object RefreshProfile : AuthUiIntent
    data object ClearMessages : AuthUiIntent
}

/**
 * One-time side effects (navigation, notifications).
 */
sealed interface AuthUiEffect {
    data object NavigateToHome : AuthUiEffect
    data object NavigateBack : AuthUiEffect
    data class ShowToast(val message: String) : AuthUiEffect
}
