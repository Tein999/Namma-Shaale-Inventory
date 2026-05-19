package com.nammashale.inventory

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.model.DashboardStats
import com.nammashale.inventory.domain.model.IssueLog
import com.nammashale.inventory.domain.repository.AssetRepository
import com.nammashale.inventory.domain.usecase.AddAssetUseCase
import com.nammashale.inventory.domain.usecase.GenerateReportUseCase
import com.nammashale.inventory.domain.usecase.SearchAssetsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ──────────────────────────────────────────────────────────────────────────────
// FAKE REPOSITORY (no Android framework dependencies)
// ──────────────────────────────────────────────────────────────────────────────

class FakeAssetRepository : AssetRepository {
    val assets = mutableListOf<Asset>()
    var nextId = 1L

    override fun getAllAssets(): Flow<List<Asset>> = flowOf(assets.toList())
    override fun getAssetById(id: Long): Flow<Asset?> = flowOf(assets.find { it.id == id })
    override fun searchAssets(query: String): Flow<List<Asset>> = flowOf(
        assets.filter { it.name.contains(query, true) || it.serialNumber.contains(query, true) }
    )
    override fun getAssetsByCondition(condition: AssetCondition): Flow<List<Asset>> =
        flowOf(assets.filter { it.condition == condition })

    override suspend fun insertAsset(asset: Asset): Long {
        val id = nextId++
        assets.add(asset.copy(id = id))
        return id
    }
    override suspend fun updateAsset(asset: Asset) {
        val idx = assets.indexOfFirst { it.id == asset.id }
        if (idx >= 0) assets[idx] = asset
    }
    override suspend fun deleteAsset(asset: Asset) { assets.removeIf { it.id == asset.id } }
    override suspend fun updateAssetCondition(id: Long, condition: AssetCondition) {
        val idx = assets.indexOfFirst { it.id == id }
        if (idx >= 0) assets[idx] = assets[idx].copy(condition = condition)
    }
    override suspend fun updateHealthCheck(id: Long, timestamp: Long) {
        val idx = assets.indexOfFirst { it.id == id }
        if (idx >= 0) assets[idx] = assets[idx].copy(lastHealthCheck = timestamp)
    }
    override fun getDashboardStats(): Flow<DashboardStats> = flowOf(
        DashboardStats(
            totalAssets = assets.size,
            workingCount = assets.count { it.condition == AssetCondition.WORKING },
            needsRepairCount = assets.count { it.condition == AssetCondition.NEEDS_REPAIR },
            brokenCount = assets.count { it.condition == AssetCondition.BROKEN }
        )
    )
    override fun getIssueLogsForAsset(assetId: Long): Flow<List<IssueLog>> = flowOf(emptyList())
    override fun getAllIssueLogs(): Flow<List<IssueLog>> = flowOf(emptyList())
    override suspend fun insertIssueLog(issueLog: IssueLog): Long = 1L
    override suspend fun deleteIssueLog(issueLog: IssueLog) {}
    override suspend fun markIssueResolved(id: Long) {}
}

// ──────────────────────────────────────────────────────────────────────────────
// UNIT TESTS
// ──────────────────────────────────────────────────────────────────────────────

class AddAssetUseCaseTest {

    private lateinit var repository: FakeAssetRepository
    private lateinit var useCase: AddAssetUseCase

    @Before
    fun setup() {
        repository = FakeAssetRepository()
        useCase = AddAssetUseCase(repository)
    }

    @Test
    fun `adding valid asset returns success with positive id`() = runTest {
        val asset = Asset(name = "Projector", serialNumber = "SN-001")
        val result = useCase(asset)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow() > 0)
        assertEquals(1, repository.assets.size)
    }

    @Test
    fun `adding asset with blank name returns failure`() = runTest {
        val asset = Asset(name = "", serialNumber = "SN-001")
        val result = useCase(asset)
        assertTrue(result.isFailure)
        assertEquals(0, repository.assets.size)
    }

    @Test
    fun `adding asset with blank serial number returns failure`() = runTest {
        val asset = Asset(name = "Laptop", serialNumber = "   ")
        val result = useCase(asset)
        assertTrue(result.isFailure)
    }

    @Test
    fun `adding multiple assets increments IDs correctly`() = runTest {
        useCase(Asset(name = "Chair", serialNumber = "CH-001"))
        useCase(Asset(name = "Desk", serialNumber = "DK-001"))
        assertEquals(2, repository.assets.size)
        assertEquals(1L, repository.assets[0].id)
        assertEquals(2L, repository.assets[1].id)
    }
}

class SearchAssetsUseCaseTest {

    private lateinit var repository: FakeAssetRepository
    private lateinit var useCase: SearchAssetsUseCase

    @Before
    fun setup() {
        repository = FakeAssetRepository()
        useCase = SearchAssetsUseCase(repository)
        // Pre-populate
        repository.assets.addAll(listOf(
            Asset(id = 1, name = "Projector", serialNumber = "PR-001", condition = AssetCondition.WORKING),
            Asset(id = 2, name = "Laptop", serialNumber = "LT-002", condition = AssetCondition.NEEDS_REPAIR),
            Asset(id = 3, name = "Chair", serialNumber = "CH-003", condition = AssetCondition.BROKEN)
        ))
    }

    @Test
    fun `empty query returns all assets`() = runTest {
        useCase("", null).collect { result ->
            assertEquals(3, result.size)
        }
    }

    @Test
    fun `query filters by name case-insensitively`() = runTest {
        useCase("laptop", null).collect { result ->
            assertEquals(1, result.size)
            assertEquals("Laptop", result[0].name)
        }
    }

    @Test
    fun `condition filter returns only matching assets`() = runTest {
        useCase("", AssetCondition.NEEDS_REPAIR).collect { result ->
            assertEquals(1, result.size)
            assertEquals(AssetCondition.NEEDS_REPAIR, result[0].condition)
        }
    }

    @Test
    fun `combined query and condition filter works`() = runTest {
        useCase("PR", AssetCondition.WORKING).collect { result ->
            assertEquals(1, result.size)
            assertEquals("Projector", result[0].name)
        }
    }
}

class DashboardStatsTest {

    @Test
    fun `health percentage is 100 when all assets are working`() {
        val stats = DashboardStats(totalAssets = 5, workingCount = 5, needsRepairCount = 0, brokenCount = 0)
        assertEquals(100f, stats.healthPercentage)
    }

    @Test
    fun `health percentage is 0 when no assets are working`() {
        val stats = DashboardStats(totalAssets = 3, workingCount = 0, needsRepairCount = 2, brokenCount = 1)
        assertEquals(0f, stats.healthPercentage)
    }

    @Test
    fun `health percentage is 100 when there are no assets`() {
        val stats = DashboardStats()
        assertEquals(100f, stats.healthPercentage)
    }

    @Test
    fun `health percentage is 50 when half are working`() {
        val stats = DashboardStats(totalAssets = 4, workingCount = 2, needsRepairCount = 1, brokenCount = 1)
        assertEquals(50f, stats.healthPercentage)
    }
}

class GenerateReportUseCaseTest {

    private val useCase = GenerateReportUseCase()

    @Test
    fun `report contains app name`() {
        val report = useCase(emptyList(), DashboardStats())
        assertTrue(report.contains("NAMMA-SHAALE"))
    }

    @Test
    fun `report shows correct total count`() {
        val assets = listOf(
            Asset(id = 1, name = "Projector", serialNumber = "SN-001"),
            Asset(id = 2, name = "Laptop", serialNumber = "SN-002")
        )
        val stats = DashboardStats(totalAssets = 2, workingCount = 2)
        val report = useCase(assets, stats)
        assertTrue(report.contains("Total Assets       : 2"))
    }

    @Test
    fun `report contains broken assets section`() {
        val assets = listOf(
            Asset(id = 1, name = "Broken Projector", serialNumber = "SN-001", condition = AssetCondition.BROKEN)
        )
        val stats = DashboardStats(totalAssets = 1, brokenCount = 1)
        val report = useCase(assets, stats)
        assertTrue(report.contains("BROKEN"))
        assertTrue(report.contains("Broken Projector"))
    }
}
