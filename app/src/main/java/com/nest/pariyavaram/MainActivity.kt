package com.nest.pariyavaram

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

import com.nest.pariyavaram.admin.navigation.AdminNavHost
import com.nest.pariyavaram.ui.navigation.Screen
import com.nest.pariyavaram.ui.navigation.bottomNavItems
import com.nest.pariyavaram.ui.screen.*
import com.nest.pariyavaram.ui.theme.ParyavaranKavaluTheme
import com.nest.pariyavaram.viewmodel.AuthViewModel
import com.nest.pariyavaram.viewmodel.ReportViewModel

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
            )
        )

        setContent {
            ParyavaranKavaluTheme {
                ParyavaranApp()
            }
        }
    }
}

@Composable
fun ParyavaranApp() {

    val navController = rememberNavController()

    val reportViewModel: ReportViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val authState by authViewModel.uiState.collectAsState()

    // ✅ START DESTINATION
    val startDestination =
        when {
            authState.currentUser != null && authState.role == "admin" -> "admin_root"
            authState.currentUser != null -> Screen.Map.route
            else -> "login"
        }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar =
        currentRoute != "login" &&
                !currentRoute.orEmpty().startsWith("admin")

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ✅ LOGIN SCREEN
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        if (authState.role == "admin") {
                            navController.navigate("admin_root") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Map.route) {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ✅ ADMIN FLOW
            composable("admin_root") {
                AdminNavHost(
                    reportViewModel = reportViewModel,
                    authViewModel = authViewModel,

                    // 🔥 FIXED LOGOUT FLOW
                    onLogoutToLogin = {
                        navController.navigate("login") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ✅ USER FLOW
            composable(Screen.Map.route) {
                MapScreen(viewModel = reportViewModel)
            }

            composable(Screen.Report.route) {
                NewReportScreen(
                    viewModel = reportViewModel,
                    onSuccess = {
                        navController.navigate(Screen.Map.route)
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(viewModel = reportViewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = reportViewModel,
                    onLogout = {
                        authViewModel.logout()

                        // 🔥 FIXED USER LOGOUT
                        navController.navigate("login") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {

        bottomNavItems.forEach { item ->

            val selected =
                currentDestination?.hierarchy?.any {
                    it.route == item.screen.route
                } == true

            NavigationBarItem(
                selected = selected,

                onClick = {
                    navController.navigate(item.screen.route) {

                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector =
                            if (selected)
                                item.selectedIcon
                            else
                                item.unselectedIcon,
                        contentDescription = item.label
                    )
                },

                label = {
                    Text(item.label)
                }
            )
        }
    }
}