package com.aura.jewelry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.aura.jewelry.core.designsystem.AuraJewelryTheme
import com.aura.jewelry.core.navigation.AuraBottomNavigation
import com.aura.jewelry.core.navigation.AuraNavHost
import com.aura.jewelry.feature.cart.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Entry Activity for AURA Fine Jewelry Android application.
 * Configured with Hilt Dependency Injection, Edge-to-Edge display,
 * Type-Safe Navigation Compose, and Luxury Dark Theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AuraJewelryTheme {
                AuraApp()
            }
        }
    }
}

@Composable
fun AuraApp() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()
    val cartUiState by cartViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AuraBottomNavigation(
                navController = navController,
                cartItemCount = cartUiState.cart?.totalQuantity ?: 0
            )
        }
    ) { innerPadding ->
        AuraNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
