package com.aura.jewelry.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.jewelry.core.common.NetworkResult
import com.aura.jewelry.core.network.model.UserSummaryDto
import com.aura.jewelry.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<AuthUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeUserSession()
    }

    private fun observeUserSession() {
        viewModelScope.launch {
            authRepository.userSession.collect { session ->
                _uiState.update { current ->
                    current.copy(
                        isLoggedIn = session.isLoggedIn,
                        user = if (session.isLoggedIn && session.userId != null) {
                            UserSummaryDto(
                                id = session.userId,
                                username = session.username.orEmpty(),
                                email = session.email.orEmpty(),
                                fullName = session.fullName,
                                roles = session.roles.toList()
                            )
                        } else null
                    )
                }
            }
        }
    }

    fun onIntent(intent: AuthUiIntent) {
        when (intent) {
            is AuthUiIntent.UpdateLoginUsernameOrEmail -> {
                _uiState.update { it.copy(loginUsernameOrEmail = intent.value, errorMessage = null) }
            }
            is AuthUiIntent.UpdateLoginPassword -> {
                _uiState.update { it.copy(loginPassword = intent.value, errorMessage = null) }
            }
            AuthUiIntent.ToggleLoginPasswordVisibility -> {
                _uiState.update { it.copy(isLoginPasswordVisible = !it.isLoginPasswordVisible) }
            }
            AuthUiIntent.SubmitLogin -> login()

            is AuthUiIntent.UpdateRegisterUsername -> {
                _uiState.update { it.copy(registerUsername = intent.value, errorMessage = null) }
            }
            is AuthUiIntent.UpdateRegisterEmail -> {
                _uiState.update { it.copy(registerEmail = intent.value, errorMessage = null) }
            }
            is AuthUiIntent.UpdateRegisterPassword -> {
                _uiState.update { it.copy(registerPassword = intent.value, errorMessage = null) }
            }
            is AuthUiIntent.UpdateRegisterConfirmPassword -> {
                _uiState.update { it.copy(registerConfirmPassword = intent.value, errorMessage = null) }
            }
            is AuthUiIntent.UpdateRegisterFirstName -> {
                _uiState.update { it.copy(registerFirstName = intent.value) }
            }
            is AuthUiIntent.UpdateRegisterLastName -> {
                _uiState.update { it.copy(registerLastName = intent.value) }
            }
            is AuthUiIntent.UpdateRegisterPhone -> {
                _uiState.update { it.copy(registerPhone = intent.value) }
            }
            AuthUiIntent.ToggleRegisterPasswordVisibility -> {
                _uiState.update { it.copy(isRegisterPasswordVisible = !it.isRegisterPasswordVisible) }
            }
            AuthUiIntent.ToggleRegisterConfirmPasswordVisibility -> {
                _uiState.update { it.copy(isRegisterConfirmPasswordVisible = !it.isRegisterConfirmPasswordVisible) }
            }
            AuthUiIntent.SubmitRegister -> register()

            AuthUiIntent.SubmitLogout -> logout()
            AuthUiIntent.RefreshProfile -> refreshProfile()
            AuthUiIntent.ClearMessages -> {
                _uiState.update { it.copy(errorMessage = null, successMessage = null) }
            }
        }
    }

    private fun login() {
        val state = _uiState.value
        if (!state.isLoginFormValid) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập tên đăng nhập và mật khẩu ít nhất 6 ký tự") }
            return
        }

        viewModelScope.launch {
            authRepository.login(
                usernameOrEmail = state.loginUsernameOrEmail.trim(),
                password = state.loginPassword
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                successMessage = "Đăng nhập thành công!",
                                loginPassword = "",
                                errorMessage = null
                            )
                        }
                        _uiEffect.send(AuthUiEffect.NavigateToHome)
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun register() {
        val state = _uiState.value
        if (!state.isRegisterFormValid) {
            val error = when {
                state.registerUsername.isBlank() -> "Vui lòng nhập tên tài khoản"
                !state.registerEmail.contains("@") -> "Email không hợp lệ"
                state.registerPassword.length < 6 -> "Mật khẩu phải từ 6 ký tự trở lên"
                state.registerPassword != state.registerConfirmPassword -> "Mật khẩu xác nhận không khớp"
                else -> "Dữ liệu đăng ký không hợp lệ"
            }
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            authRepository.register(
                username = state.registerUsername.trim(),
                email = state.registerEmail.trim(),
                password = state.registerPassword,
                firstName = state.registerFirstName.trim().ifEmpty { null },
                lastName = state.registerLastName.trim().ifEmpty { null },
                phone = state.registerPhone.trim().ifEmpty { null }
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                successMessage = "Đăng ký thành công!",
                                registerPassword = "",
                                registerConfirmPassword = "",
                                errorMessage = null
                            )
                        }
                        _uiEffect.send(AuthUiEffect.NavigateToHome)
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                if (result is NetworkResult.Success) {
                    _uiEffect.send(AuthUiEffect.ShowToast("Đã đăng xuất an toàn"))
                    _uiEffect.send(AuthUiEffect.NavigateToHome)
                }
            }
        }
    }

    private fun refreshProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(user = result.data) }
                }
            }
        }
    }
}
