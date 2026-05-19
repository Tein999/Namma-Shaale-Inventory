package com.nammashale.inventory.presentation.navigation

/**
 * Sealed class representing all screens/routes in the app.
 * Using sealed class ensures compile-time safety for navigation destinations.
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AssetList : Screen("asset_list")
    object AddAsset : Screen("add_asset")
    object AssetDetail : Screen("asset_detail/{assetId}") {
        fun createRoute(assetId: Long) = "asset_detail/$assetId"
    }
    object IssueLog : Screen("issue_log/{assetId}") {
        fun createRoute(assetId: Long) = "issue_log/$assetId"
    }
    object AllIssueLogs : Screen("all_issue_logs")
    object Camera : Screen("camera")
    object Report : Screen("report")
}
