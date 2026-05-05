package com.nest.pariyavaram.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

// ─────────────────────────────────────────────────────────────────────────────
//  ROUTES
// ─────────────────────────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Map     : Screen("map")
    object Report  : Screen("report")
    object History : Screen("history")
    object Profile : Screen("profile")
}

// ─────────────────────────────────────────────────────────────────────────────
//  BOTTOM NAV ITEMS
// ─────────────────────────────────────────────────────────────────────────────
data class BottomNavItem(
    val screen      : Screen,
    val label       : String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen        = Screen.Map,
        label         = "Map",
        selectedIcon  = Icons.Filled.Map,
        unselectedIcon= Icons.Outlined.Map
    ),
    BottomNavItem(
        screen        = Screen.Report,
        label         = "Report",
        selectedIcon  = Icons.Filled.AddCircle,
        unselectedIcon= Icons.Outlined.AddCircle
    ),
    BottomNavItem(
        screen        = Screen.History,
        label         = "History",
        selectedIcon  = Icons.Filled.History,
        unselectedIcon= Icons.Outlined.History
    ),
    BottomNavItem(
        screen        = Screen.Profile,
        label         = "Profile",
        selectedIcon  = Icons.Filled.Person,
        unselectedIcon= Icons.Outlined.Person
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
//  SCREEN TRANSITIONS
// ─────────────────────────────────────────────────────────────────────────────
fun enterTransition(): EnterTransition =
    fadeIn(animationSpec = tween(250)) +
            slideInHorizontally(animationSpec = tween(250)) { it / 8 }

fun exitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(200)) +
            slideOutHorizontally(animationSpec = tween(200)) { -it / 8 }