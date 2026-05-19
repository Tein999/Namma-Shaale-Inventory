package com.nammashale.inventory.data.repository

import com.nammashale.inventory.data.local.dao.AssetDao
import com.nammashale.inventory.data.local.dao.IssueLogDao
import com.nammashale.inventory.data.local.entity.AssetEntity
import com.nammashale.inventory.data.local.entity.IssueLogEntity
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.model.DashboardStats
import com.nammashale.inventory.domain.model.IssueLog
import com.nammashale.inventory.domain.repository.AssetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of AssetRepository.
 * Bridges domain models and Room entities.
 */
@Singleton
class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao,
    private val issueLogDao: IssueLogDao
) : AssetRepository {

    // ─── Assets ───────────────────────────────────────────────────────────────

    override fun getAllAssets(): Flow<List<Asset>> =
        assetDao.getAllAssets().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAssetById(id: Long): Flow<Asset?> =
        assetDao.getAssetById(id).map { it?.toDomain() }

    override fun searchAssets(query: String): Flow<List<Asset>> =
        assetDao.searchAssets(query).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAssetsByCondition(condition: AssetCondition): Flow<List<Asset>> =
        assetDao.getAssetsByCondition(condition.name).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertAsset(asset: Asset): Long =
        assetDao.insertAsset(AssetEntity.fromDomain(asset))

    override suspend fun updateAsset(asset: Asset) =
        assetDao.updateAsset(AssetEntity.fromDomain(asset))

    override suspend fun deleteAsset(asset: Asset) =
        assetDao.deleteAsset(AssetEntity.fromDomain(asset))

    override suspend fun updateAssetCondition(id: Long, condition: AssetCondition) =
        assetDao.updateAssetCondition(id, condition.name)

    override suspend fun updateHealthCheck(id: Long, timestamp: Long) =
        assetDao.updateHealthCheck(id, timestamp)

    // ─── Dashboard Stats ──────────────────────────────────────────────────────

    override fun getDashboardStats(): Flow<DashboardStats> {
        // Combine the total count with per-condition counts into a single stats object
        return combine(
            assetDao.getTotalCount(),
            assetDao.getCountByCondition(AssetCondition.WORKING.name),
            assetDao.getCountByCondition(AssetCondition.NEEDS_REPAIR.name),
            assetDao.getCountByCondition(AssetCondition.BROKEN.name)
        ) { total, working, needsRepair, broken ->
            DashboardStats(
                totalAssets = total,
                workingCount = working,
                needsRepairCount = needsRepair,
                brokenCount = broken
            )
        }
    }

    // ─── Issue Logs ───────────────────────────────────────────────────────────

    override fun getIssueLogsForAsset(assetId: Long): Flow<List<IssueLog>> =
        issueLogDao.getIssueLogsForAsset(assetId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAllIssueLogs(): Flow<List<IssueLog>> =
        issueLogDao.getAllIssueLogs().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertIssueLog(issueLog: IssueLog): Long =
        issueLogDao.insertIssueLog(IssueLogEntity.fromDomain(issueLog))

    override suspend fun deleteIssueLog(issueLog: IssueLog) =
        issueLogDao.deleteIssueLog(IssueLogEntity.fromDomain(issueLog))

    override suspend fun markIssueResolved(id: Long) =
        issueLogDao.markIssueResolved(id)
}
