package com.nammashale.inventory.domain.repository

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.model.DashboardStats
import com.nammashale.inventory.domain.model.IssueLog
import kotlinx.coroutines.flow.Flow

/**
 * Contract that defines all data operations for assets.
 * The data layer implements this interface.
 * ViewModels depend ONLY on this interface, not the implementation.
 */
interface AssetRepository {

    // ─── Assets ───────────────────────────────────────────────
    fun getAllAssets(): Flow<List<Asset>>
    fun getAssetById(id: Long): Flow<Asset?>
    fun searchAssets(query: String): Flow<List<Asset>>
    fun getAssetsByCondition(condition: AssetCondition): Flow<List<Asset>>
    suspend fun insertAsset(asset: Asset): Long
    suspend fun updateAsset(asset: Asset)
    suspend fun deleteAsset(asset: Asset)
    suspend fun updateAssetCondition(id: Long, condition: AssetCondition)
    suspend fun updateHealthCheck(id: Long, timestamp: Long)

    // ─── Stats ────────────────────────────────────────────────
    fun getDashboardStats(): Flow<DashboardStats>

    // ─── Issue Logs ───────────────────────────────────────────
    fun getIssueLogsForAsset(assetId: Long): Flow<List<IssueLog>>
    fun getAllIssueLogs(): Flow<List<IssueLog>>
    suspend fun insertIssueLog(issueLog: IssueLog): Long
    suspend fun deleteIssueLog(issueLog: IssueLog)
    suspend fun markIssueResolved(id: Long)
}
