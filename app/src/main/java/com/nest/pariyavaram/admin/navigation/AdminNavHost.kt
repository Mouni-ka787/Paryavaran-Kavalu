package com.nest.pariyavaram.admin.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.nest.pariyavaram.admin.screen.*
import com.nest.pariyavaram.viewmodel.*

@Composable
fun AdminNavHost(
    reportViewModel: ReportViewModel,
    authViewModel: AuthViewModel,
    onLogoutToLogin: () -> Unit
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                adminBottomItems.forEach { item ->

                    val currentRoute =
                        navController.currentBackStackEntryAsState().value?.destination?.route

                    NavigationBarItem(
                        selected = currentRoute == item.route,

                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },

                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "admin_reports",
            modifier = Modifier.padding(padding)
        ) {

            // REPORTS
            composable("admin_reports") {
                AdminReportsScreen(
                    viewModel = reportViewModel,
                    onBack = {},
                    onReportClick = { id ->
                        navController.navigate("admin_detail/$id")
                    }
                )
            }

            // USERS
            composable("admin_users") {
                AdminUsersScreen(onBack = {})
            }

            // MAP
            composable("admin_map") {
                AdminMapScreen(
                    viewModel = reportViewModel,
                    onBack = {}
                )
            }

            // PROFILE ✅
            composable("admin_profile") {
                AdminProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        authViewModel.logout()
                        onLogoutToLogin()
                    }
                )
            }

            // DETAIL
            composable("admin_detail/{reportId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("reportId")?.toInt() ?: 0

                AdminReportDetailScreen(
                    reportId = id,
                    viewModel = reportViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}