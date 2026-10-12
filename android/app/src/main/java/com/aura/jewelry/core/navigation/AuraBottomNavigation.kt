package com.aura.jewelry.core.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aura.jewelry.core.designsystem.AuraBorderDark
import com.aura.jewelry.core.designsystem.AuraGoldContainer
import com.aura.jewelry.core.designsystem.AuraGoldPrimary
import com.aura.jewelry.core.designsystem.AuraSakuraPrimary
import com.aura.jewelry.core.designsystem.AuraSurfaceDark
import com.aura.jewelry.core.designsystem.AuraTextMuted
import com.aura.jewelry.core.designsystem.AuraTextPrimary

/**
 * Luxury Bottom Navigation Bar for AURA Jewelry.
 * Adheres to Dark Cinematic Luxury design with Gold indicators and Sakura alert badges.
 */
@Composable
fun AuraBottomNavigation(
    navController: NavHostController,
    cartItemCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only display bottom navigation on top-level destinations
    val showBottomBar = BottomNavItem.entries.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    if (showBottomBar) {
        NavigationBar(
            modifier = modifier.border(width = 0.5.dp, color = AuraBorderDark),
            containerColor = com.aura.jewelry.core.designsystem.AuraBackgroundDeep,
            contentColor = AuraTextPrimary,
            tonalElevation = 0.dp
        ) {
            BottomNavItem.entries.forEach { item ->
                val isSelected = currentDestination?.hierarchy?.any {
                    it.hasRoute(item.route::class)
                } == true

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        if (item == BottomNavItem.CART && cartItemCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = AuraSakuraPrimary,
                                        contentColor = androidx.compose.ui.graphics.Color.White
                                    ) {
                                        Text(
                                            text = if (cartItemCount > 99) "99+" else "$cartItemCount",
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.aura.jewelry.core.designsystem.AuraGold400,
                        selectedTextColor = com.aura.jewelry.core.designsystem.AuraGold400,
                        unselectedIconColor = AuraTextMuted,
                        unselectedTextColor = AuraTextMuted,
                        indicatorColor = AuraGoldContainer
                    )
                )
            }
        }
    }
}
