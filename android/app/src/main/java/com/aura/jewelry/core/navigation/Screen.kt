package com.aura.jewelry.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// ============================================================================
// TYPE-SAFE NAVIGATION ROUTES (Navigation Compose 2.8+)
// ============================================================================

sealed interface Route

// Bottom Bar Destinations
@Serializable
data object HomeRoute : Route

@Serializable
data object CatalogRoute : Route

@Serializable
data object WishlistRoute : Route

@Serializable
data object CartRoute : Route

@Serializable
data object ProfileRoute : Route

// Detail & Nested Screens
@Serializable
data class ProductDetailRoute(val slug: String) : Route

@Serializable
data object CheckoutRoute : Route

@Serializable
data class OrderDetailRoute(val orderCode: String) : Route

@Serializable
data object OrderHistoryRoute : Route

@Serializable
data object LoginRoute : Route

@Serializable
data object RegisterRoute : Route

// ============================================================================
// BOTTOM NAVIGATION ITEM DEFINITIONS
// ============================================================================

enum class BottomNavItem(
    val title: String,
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        title = "Trang Chủ",
        route = HomeRoute,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    CATALOG(
        title = "Bộ Sưu Tập",
        route = CatalogRoute,
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category
    ),
    WISHLIST(
        title = "Yêu Thích",
        route = WishlistRoute,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    CART(
        title = "Túi Đồ",
        route = CartRoute,
        selectedIcon = Icons.Filled.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag
    ),
    PROFILE(
        title = "Tài Khoản",
        route = ProfileRoute,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
