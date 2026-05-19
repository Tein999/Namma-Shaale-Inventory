package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.repository.AssetRepository
import javax.inject.Inject

class UpdateAssetConditionUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    suspend operator fun invoke(assetId: Long, condition: AssetCondition) {
        repository.updateAssetCondition(assetId, condition)
    }
}
