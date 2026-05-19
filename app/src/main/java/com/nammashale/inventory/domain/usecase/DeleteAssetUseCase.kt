package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.repository.AssetRepository
import javax.inject.Inject

class DeleteAssetUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    suspend operator fun invoke(asset: Asset) {
        repository.deleteAsset(asset)
    }
}
