package com.aura.jewelry.feature.checkout.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aura.jewelry.core.designsystem.AuraDarkBackground
import com.aura.jewelry.core.designsystem.AuraDarkSurface
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraMutedText
import com.aura.jewelry.feature.checkout.CheckoutUiEvent
import com.aura.jewelry.feature.checkout.CheckoutUiIntent
import com.aura.jewelry.feature.checkout.CheckoutViewModel
import com.aura.jewelry.feature.checkout.ui.components.CheckoutPriceSummary
import com.aura.jewelry.feature.checkout.ui.components.DeliveryAddressSection
import com.aura.jewelry.feature.checkout.ui.components.OrderSuccessDialog
import com.aura.jewelry.feature.checkout.ui.components.PaymentMethodSelector
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CheckoutUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is CheckoutUiEvent.OrderPlacedSuccess -> {
                    // Handled through uiState.orderSuccess dialog
                }
                is CheckoutUiEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "THANH TOÁN ĐƠN HÀNG",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = com.aura.jewelry.core.designsystem.AuraSerifFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp
                        ),
                        color = AuraGoldPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = AuraGoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.aura.jewelry.core.designsystem.AuraBackgroundPure
                ),
                modifier = Modifier.border(0.5.dp, com.aura.jewelry.core.designsystem.AuraBorderDark)
            )
        },
        bottomBar = {
            Surface(
                color = com.aura.jewelry.core.designsystem.AuraBackgroundDeep,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, com.aura.jewelry.core.designsystem.AuraBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tổng số tiền cần trả:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuraMutedText
                        )
                        Text(
                            text = currencyFormat.format(uiState.grandTotal),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = com.aura.jewelry.core.designsystem.AuraGold400
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sakura Pill Order Button (.btn-pill-sakura matching web)
                    com.aura.jewelry.core.designsystem.AuraSakuraButton(
                        onClick = { viewModel.onIntent(CheckoutUiIntent.PlaceOrder) },
                        enabled = !uiState.isPlacingOrder && !uiState.isLoadingCart,
                        isLoading = uiState.isPlacingOrder,
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
                            text = "XÁC NHẬN ĐẶT HÀNG VIP",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AuraDarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Thông tin địa chỉ nhận hàng
                DeliveryAddressSection(
                    uiState = uiState,
                    onIntent = viewModel::onIntent
                )

                // Section 2: Chọn phương thức thanh toán
                PaymentMethodSelector(
                    selectedMethod = uiState.selectedPaymentMethod,
                    onSelectMethod = { viewModel.onIntent(CheckoutUiIntent.SelectPaymentMethod(it)) }
                )

                // Section 3: Tổng kết chi phí & danh sách trang sức
                CheckoutPriceSummary(uiState = uiState)

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Success Dialog khi đặt hàng thành công
            uiState.orderSuccess?.let { order ->
                OrderSuccessDialog(
                    order = order,
                    onViewOrderDetails = { orderCode ->
                        viewModel.onIntent(CheckoutUiIntent.ResetSuccess)
                        onNavigateToOrderDetail(orderCode)
                    },
                    onContinueShopping = {
                        viewModel.onIntent(CheckoutUiIntent.ResetSuccess)
                        onNavigateToHome()
                    }
                )
            }
        }
    }
}
