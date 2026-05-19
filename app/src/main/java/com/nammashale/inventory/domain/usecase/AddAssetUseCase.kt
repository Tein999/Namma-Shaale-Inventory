package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.repository.AssetRepository
import javax.inject.Inject

/**
 * Use case for adding a new asset.
 * Contains input validation business rules.
 */
class AddAssetUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    /**
     * @return Result<Long> — the new asset ID on success, or error on failure.
     */
    suspend operator fun invoke(asset: Asset): Result<Long> {
        // Business rule: name cannot be blank
        if (asset.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Asset name cannot be empty"))
        }
        // Business rule: serial number should be provided
        if (asset.serialNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("Serial number cannot be empty"))
        }
        return try {
            val id = repository.insertAsset(asset)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
