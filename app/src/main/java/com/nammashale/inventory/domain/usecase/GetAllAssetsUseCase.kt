package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAssetsUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    operator fun invoke(): Flow<List<Asset>> = repository.getAllAssets()
}
