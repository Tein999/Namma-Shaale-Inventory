package com.nammashale.inventory.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.DashboardStats
import com.nammashale.inventory.domain.usecase.GenerateReportUseCase
import com.nammashale.inventory.domain.usecase.GetAllAssetsUseCase
import com.nammashale.inventory.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUiState(
    val reportText: String = "",
    val isLoading: Boolean = true,
    val isGenerating: Boolean = false,
    val isCopied: Boolean = false,
    val assets: List<Asset> = emptyList(),
    val stats: DashboardStats = DashboardStats()
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getAllAssetsUseCase: GetAllAssetsUseCase,
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val generateReportUseCase: GenerateReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getAllAssetsUseCase(),
                getDashboardStatsUseCase()
            ) { assets, stats ->
                Pair(assets, stats)
            }.collect { (assets, stats) ->
                val report = generateReportUseCase(assets, stats)
                _uiState.value = ReportUiState(
                    reportText = report,
                    assets = assets,
                    stats = stats,
                    isLoading = false,
                    isGenerating = false
                )
            }
        }
    }

    /** Triggered by the Refresh button. Regenerates the report text. */
    fun generateReport() {
        val state = _uiState.value
        _uiState.value = state.copy(isGenerating = true)
        val newReport = generateReportUseCase(state.assets, state.stats)
        _uiState.value = state.copy(reportText = newReport, isGenerating = false)
    }

    /** Called when the user taps Copy — shows a confirmation snackbar. */
    fun onCopied() {
        _uiState.value = _uiState.value.copy(isCopied = true)
    }
}
