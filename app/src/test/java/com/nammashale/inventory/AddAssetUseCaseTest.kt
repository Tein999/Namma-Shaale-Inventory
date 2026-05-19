package com.nammashale.inventory

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.usecase.AddAssetUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AddAssetUseCase — demonstrates validation business rules.
 *
 * To run:  ./gradlew test
 */
class AddAssetUseCaseTest {

    private lateinit var fakeRepository: FakeAssetRepository
    private lateinit var addAssetUseCase: AddAssetUseCase

    @Before
    fun setup() {
        fakeRepository = FakeAssetRepository()
        addAssetUseCase = AddAssetUseCase(fakeRepository)
    }

    @Test
    fun `addAsset with valid data succeeds`() = runTest {
        val asset = Asset(
            name = "Laptop",
            serialNumber = "SN-001",
            condition = AssetCondition.WORKING
        )
        val result = addAssetUseCase(asset)
        assertTrue("Should succeed with valid data", result.isSuccess)
    }

    @Test
    fun `addAsset with blank name fails`() = runTest {
        val asset = Asset(name = "", serialNumber = "SN-001")
        val result = addAssetUseCase(asset)
        assertTrue("Should fail with blank name", result.isFailure)
        assertEquals("Asset name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addAsset with blank serial number fails`() = runTest {
        val asset = Asset(name = "Projector", serialNumber = "")
        val result = addAssetUseCase(asset)
        assertTrue("Should fail with blank serial number", result.isFailure)
        assertEquals("Serial number cannot be empty", result.exceptionOrNull()?.message)
    }
}
