package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.model.DashboardStats
import com.nammashale.inventory.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    operator fun invoke(): Flow<DashboardStats> = repository.getDashboardStats()
}
