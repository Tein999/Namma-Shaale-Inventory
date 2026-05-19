package com.nammashale.inventory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nammashale.inventory.presentation.addasset.AddAssetScreen
import com.nammashale.inventory.presentation.assetdetail.AssetDetailScreen
import com.nammashale.inventory.presentation.assetlist.AssetListScreen
import com.nammashale.inventory.presentation.camera.CameraScreen
import com.nammashale.inventory.presentation.dashboard.DashboardScreen
import com.nammashale.inventory.presentation.issuelog.AllIssueLogsScreen
import com.nammashale.inventory.presentation.issuelog.IssueLogScreen
import com.nammashale.inventory.presentation.report.ReportScreen

/**
 * Defines the complete navigation graph for the app.
 * All screen-to-screen navigation goes through here.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    capturedPhotoPath: String?,
    onPhotoPathConsumed: () -> Unit,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAssets = { navController.navigate(Screen.AssetList.route) },
                onNavigateToAddAsset = { navController.navigate(Screen.AddAsset.route) },
                onNavigateToReport = { navController.navigate(Screen.Report.route) },
                onNavigateToAllIssues = { navController.navigate(Screen.AllIssueLogs.route) }
            )
        }

        composable(Screen.AssetList.route) {
            AssetListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddAsset = { navController.navigate(Screen.AddAsset.route) },
                onNavigateToDetail = { assetId ->
                    navController.navigate(Screen.AssetDetail.createRoute(assetId))
                }
            )
        }

        composable(Screen.AddAsset.route) {
            AddAssetScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
                capturedPhotoPath = capturedPhotoPath,
                onPhotoPathConsumed = onPhotoPathConsumed
            )
        }

        composable(
            route = Screen.AssetDetail.route,
            arguments = listOf(
                navArgument("assetId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getLong("assetId") ?: return@composable
            AssetDetailScreen(
                assetId = assetId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIssueLog = {
                    navController.navigate(Screen.IssueLog.createRoute(assetId))
                }
            )
        }

        composable(
            route = Screen.IssueLog.route,
            arguments = listOf(
                navArgument("assetId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getLong("assetId") ?: return@composable
            IssueLogScreen(
                assetId = assetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AllIssueLogs.route) {
            AllIssueLogsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onPhotoTaken = { photoPath ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("capturedPhotoPath", photoPath)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Report.route) {
            ReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
