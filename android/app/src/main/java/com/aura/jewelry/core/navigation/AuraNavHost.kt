package com.aura.jewelry.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.aura.jewelry.core.designsystem.AuraBackgroundDark
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextPrimary

/**
 * Main NavHost configuring Type-Safe routing and deep link handlers.
 */
@Composable
fun AuraNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        }
    ) {
        composable<HomeRoute> {
            val homeViewModel: com.aura.jewelry.feature.home.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.home.ui.HomeScreen(
                viewModel = homeViewModel,
                onNavigateToDetail = { slug ->
                    navController.navigate(ProductDetailRoute(slug))
                },
                onNavigateToCart = {
                    navController.navigate(CartRoute)
                }
            )
        }

        composable<CatalogRoute> {
            val catalogViewModel: com.aura.jewelry.feature.catalog.CatalogViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.catalog.ui.CatalogScreen(
                viewModel = catalogViewModel,
                onNavigateToDetail = { slug -> navController.navigate(ProductDetailRoute(slug)) }
            )
        }

        composable<WishlistRoute> {
            val wishlistViewModel: com.aura.jewelry.feature.wishlist.WishlistViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.wishlist.ui.WishlistScreen(
                viewModel = wishlistViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProduct = { slug -> navController.navigate(ProductDetailRoute(slug)) },
                onNavigateToCatalog = { navController.navigate(CatalogRoute) }
            )
        }

        composable<CartRoute> {
            val cartViewModel: com.aura.jewelry.feature.cart.CartViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.cart.ui.CartScreen(
                viewModel = cartViewModel,
                onNavigateToCheckout = { navController.navigate(CheckoutRoute) },
                onNavigateToLogin = { navController.navigate(LoginRoute) },
                onNavigateToCatalog = { navController.navigate(CatalogRoute) },
                onNavigateToProduct = { slug -> navController.navigate(ProductDetailRoute(slug)) }
            )
        }

        composable<ProfileRoute> {
            val authViewModel: com.aura.jewelry.feature.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.auth.ui.ProfileScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(LoginRoute) },
                onNavigateToRegister = { navController.navigate(RegisterRoute) },
                onNavigateToOrders = { navController.navigate(OrderHistoryRoute) },
                onNavigateToWishlist = { navController.navigate(WishlistRoute) }
            )
        }

        composable<ProductDetailRoute>(
            deepLinks = listOf(
                navDeepLink<ProductDetailRoute>(basePath = "aura://product"),
                navDeepLink<ProductDetailRoute>(basePath = "https://aurajewelry.com/products")
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetailRoute>()
            val detailViewModel: com.aura.jewelry.feature.detail.ProductDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.detail.ui.ProductDetailScreen(
                slug = route.slug,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(CartRoute) },
                onNavigateToCheckout = { navController.navigate(CheckoutRoute) }
            )
        }

        composable<CheckoutRoute> {
            val checkoutViewModel: com.aura.jewelry.feature.checkout.CheckoutViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.checkout.ui.CheckoutScreen(
                viewModel = checkoutViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOrderDetail = { orderCode ->
                    navController.navigate(OrderDetailRoute(orderCode)) {
                        popUpTo(CheckoutRoute) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<OrderDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<OrderDetailRoute>()
            val orderDetailViewModel: com.aura.jewelry.feature.order.OrderDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.order.ui.OrderDetailScreen(
                orderCode = route.orderCode,
                viewModel = orderDetailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<OrderHistoryRoute> {
            val orderHistoryViewModel: com.aura.jewelry.feature.order.OrderHistoryViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.order.ui.OrderHistoryScreen(
                viewModel = orderHistoryViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { orderCode ->
                    navController.navigate(OrderDetailRoute(orderCode))
                }
            )
        }

        composable<LoginRoute> {
            val authViewModel: com.aura.jewelry.feature.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.auth.ui.LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterRoute)
                }
            )
        }

        composable<RegisterRoute> {
            val authViewModel: com.aura.jewelry.feature.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            com.aura.jewelry.feature.auth.ui.RegisterScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onNavigateToDetail: (() -> Unit)? = null,
    buttonTitle: String = "Xem Chi Tiết Mẫu"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AuraSurfaceDark)
                .border(1.dp, AuraGoldPrimary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AuraGoldPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = AuraGoldPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (onNavigateToDetail != null) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onNavigateToDetail,
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraGoldPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraGoldPrimary)
            ) {
                Text(text = buttonTitle)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContainerScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = AuraGoldPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = AuraGoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraSurfaceDark,
                    titleContentColor = AuraGoldPrimary
                )
            )
        },
        containerColor = AuraBackgroundDark
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = AuraBackgroundDark
        ) {
            content()
        }
    }
}
