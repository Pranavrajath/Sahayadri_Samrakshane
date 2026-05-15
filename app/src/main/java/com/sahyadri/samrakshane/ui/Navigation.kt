package com.sahyadri.samrakshane.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sahyadri.samrakshane.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object ReportAlert : Screen("report_alert")
    object Camera : Screen("camera/{alertType}") {
        fun createRoute(alertType: String) = "camera/$alertType"
    }
    object AlertDetail : Screen("alert_detail/{alertId}") {
        fun createRoute(alertId: Long) = "alert_detail/$alertId"
    }
    object AlertsList : Screen("alerts_list")
    object Education : Screen("education")
    object Map : Screen("map")
}

@Composable
fun SahyadriNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onReportAlert = { navController.navigate(Screen.ReportAlert.route) },
                onViewAlerts = { navController.navigate(Screen.AlertsList.route) },
                onViewMap = { navController.navigate(Screen.Map.route) },
                onViewEducation = { navController.navigate(Screen.Education.route) }
            )
        }

        composable(Screen.ReportAlert.route) {
            ReportAlertScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenCamera = { alertType ->
                    navController.navigate(Screen.Camera.createRoute(alertType))
                },
                onAlertSubmitted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ReportAlert.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Camera.route,
            arguments = listOf(navArgument("alertType") { type = NavType.StringType })
        ) { backStackEntry ->
            val alertType = backStackEntry.arguments?.getString("alertType") ?: "FOREST_FIRE"
            CameraScreen(
                alertType = alertType,
                onPhotoCaptured = { photoUri ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("photo_uri", photoUri)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AlertsList.route) {
            AlertsListScreen(
                onNavigateBack = { navController.popBackStack() },
                onAlertClick = { alertId ->
                    navController.navigate(Screen.AlertDetail.createRoute(alertId))
                }
            )
        }

        composable(
            route = Screen.AlertDetail.route,
            arguments = listOf(navArgument("alertId") { type = NavType.LongType })
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getLong("alertId") ?: 0L
            AlertDetailScreen(
                alertId = alertId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Education.route) {
            EducationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                onNavigateBack = { navController.popBackStack() },
                onAlertClick = { alertId ->
                    navController.navigate(Screen.AlertDetail.createRoute(alertId))
                }
            )
        }
    }
}
