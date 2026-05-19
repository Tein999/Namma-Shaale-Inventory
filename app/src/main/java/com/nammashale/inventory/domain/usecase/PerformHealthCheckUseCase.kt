package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.repository.AssetRepository
import javax.inject.Inject

/**
 * Marks all assets as "health-checked" at the current timestamp.
 * This represents the Monthly Health Check workflow.
 */
class PerformHealthCheckUseCase @Inject constructor(
    private val repository: AssetRepository,
    private val getAllAssetsUseCase: GetAllAssetsUseCase
) {
    suspend operator fun invoke(): Int {
        var count = 0
        val timestamp = System.currentTimeMillis()
        // We collect once to get the current snapshot
        getAllAssetsUseCase().collect { assets ->
            assets.forEach { asset ->
                repository.updateHealthCheck(asset.id, timestamp)
                count++
            }
            // Return after first emission
            return@collect
        }
        return count
    }
}
