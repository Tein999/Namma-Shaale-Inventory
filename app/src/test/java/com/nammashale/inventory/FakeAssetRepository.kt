package com.nammashale.inventory

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.model.DashboardStats
import com.nammashale.inventory.domain.model.IssueLog
import com.nammashale.inventory.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory fake implementation of AssetRepository for unit tests.
 * No Room DB required — tests run on JVM without instrumentation.
 */
class FakeAssetRepository : AssetRepository {

    private val assets = mutableListOf<Asset>()
    private val issues = mutableListOf<IssueLog>()
    private val assetsFlow = MutableStateFlow<List<Asset>>(emptyList())
    private var nextId = 1L

    override fun getAllAssets(): Flow<List<Asset>> = assetsFlow

    override fun getAssetById(id: Long): Flow<Asset?> =
        assetsFlow.map { it.find { a -> a.id == id } }

    override fun searchAssets(query: String): Flow<List<Asset>> =
        assetsFlow.map { list ->
            list.filter { it.name.contains(query, true) || it.serialNumber.contains(query, true) }
        }

    override fun getAssetsByCondition(condition: AssetCondition): Flow<List<Asset>> =
        assetsFlow.map { list -> list.filter { it.condition == condition } }

    override suspend fun insertAsset(asset: Asset): Long {
        val newAsset = asset.copy(id = nextId++)
        assets.add(newAsset)
        assetsFlow.value = assets.toList()
        return newAsset.id
    }

    override suspend fun updateAsset(asset: Asset) {
        val index = assets.indexOfFirst { it.id == asset.id }
        if (index != -1) {
            assets[index] = asset
            assetsFlow.value = assets.toList()
        }
    }

    override suspend fun deleteAsset(asset: Asset) {
        assets.removeAll { it.id == asset.id }
        assetsFlow.value = assets.toList()
    }

    override suspend fun updateAssetCondition(id: Long, condition: AssetCondition) {
        val index = assets.indexOfFirst { it.id == id }
        if (index != -1) {
            assets[index] = assets[index].copy(condition = condition)
            assetsFlow.value = assets.toList()
        }
    }

    override suspend fun updateHealthCheck(id: Long, timestamp: Long) {
        val index = assets.indexOfFirst { it.id == id }
        if (index != -1) {
            assets[index] = assets[index].copy(lastHealthCheck = timestamp)
            assetsFlow.value = assets.toList()
        }
    }

    override fun getDashboardStats(): Flow<DashboardStats> =
        assetsFlow.map { list ->
            DashboardStats(
                totalAssets = list.size,
                workingCount = list.count { it.condition == AssetCondition.WORKING },
                needsRepairCount = list.count { it.condition == AssetCondition.NEEDS_REPAIR },
                brokenCount = list.count { it.condition == AssetCondition.BROKEN }
            )
        }

    override fun getIssueLogsForAsset(assetId: Long): Flow<List<IssueLog>> =
        MutableStateFlow(issues.filter { it.assetId == assetId })

    override fun getAllIssueLogs(): Flow<List<IssueLog>> = MutableStateFlow(issues.toList())

    override suspend fun insertIssueLog(issueLog: IssueLog): Long {
        val newIssue = issueLog.copy(id = nextId++)
        issues.add(newIssue)
        return newIssue.id
    }

    override suspend fun deleteIssueLog(issueLog: IssueLog) {
        issues.removeAll { it.id == issueLog.id }
    }

    override suspend fun markIssueResolved(id: Long) {
        val index = issues.indexOfFirst { it.id == id }
        if (index != -1) issues[index] = issues[index].copy(isResolved = true)
    }
}
