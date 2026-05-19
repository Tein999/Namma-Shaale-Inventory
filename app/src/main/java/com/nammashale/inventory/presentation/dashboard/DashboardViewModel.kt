package com.nammashale.inventory.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.DashboardStats
import com.nammashale.inventory.domain.usecase.GetAllAssetsUseCase
import com.nammashale.inventory.domain.usecase.GetDashboardStatsUseCase
import com.nammashale.inventory.domain.usecase.PerformHealthCheckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val healthCheckMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val performHealthCheckUseCase: PerformHealthCheckUseCase,
    private val getAllAssetsUseCase: GetAllAssetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val stats: StateFlow<DashboardStats> = getDashboardStatsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    val recentAssets: StateFlow<List<Asset>> = getAllAssetsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun performHealthCheck() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val count = performHealthCheckUseCase()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    healthCheckMessage = "Health check completed for $count asset(s)!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to perform health check"
                )
            }
        }
    }

    fun clearHealthCheckMessage() {
        _uiState.value = _uiState.value.copy(healthCheckMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
