package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Handles search by query string AND optional condition filter.
 * Uses in-memory filtering on top of the DB flow for instant responsiveness.
 */
class SearchAssetsUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    operator fun invoke(
        query: String,
        conditionFilter: AssetCondition? = null
    ): Flow<List<Asset>> {
        return repository.getAllAssets().map { assets ->
            assets.filter { asset ->
                val matchesQuery = query.isBlank() ||
                    asset.name.contains(query, ignoreCase = true) ||
                    asset.serialNumber.contains(query, ignoreCase = true) ||
                    asset.description.contains(query, ignoreCase = true) ||
                    asset.location.contains(query, ignoreCase = true)

                val matchesCondition = conditionFilter == null ||
                    asset.condition == conditionFilter

                matchesQuery && matchesCondition
            }
        }
    }
}
