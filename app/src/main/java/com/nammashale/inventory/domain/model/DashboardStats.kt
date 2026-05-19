package com.nammashale.inventory.domain.model

/**
 * Aggregated statistics for the dashboard screen.
 */
data class DashboardStats(
    val totalAssets: Int = 0,
    val workingCount: Int = 0,
    val needsRepairCount: Int = 0,
    val brokenCount: Int = 0,
    val lastHealthCheckDate: Long? = null
) {
    val healthPercentage: Float
        get() = if (totalAssets == 0) 100f
                else (workingCount.toFloat() / totalAssets.toFloat()) * 100f
}
