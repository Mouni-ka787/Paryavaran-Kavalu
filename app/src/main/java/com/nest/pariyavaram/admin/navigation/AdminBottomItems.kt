package com.nest.pariyavaram.admin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminScreen(val route: String, val icon: ImageVector, val label: String) {
    object Reports : AdminScreen("admin_reports", Icons.Default.List, "Reports")
    object Users : AdminScreen("admin_users", Icons.Default.Person, "Users")
    object Map : AdminScreen("admin_map", Icons.Default.Map, "Map")
    object Profile : AdminScreen("admin_profile", Icons.Default.Person, "Profile")
}

val adminBottomItems = listOf(
    AdminScreen.Reports,
    AdminScreen.Users,
    AdminScreen.Map,
    AdminScreen.Profile
)