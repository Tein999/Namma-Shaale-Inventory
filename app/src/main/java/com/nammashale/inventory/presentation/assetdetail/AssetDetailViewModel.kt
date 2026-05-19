package com.nammashale.inventory.presentation.assetdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.repository.AssetRepository
import com.nammashale.inventory.domain.usecase.DeleteAssetUseCase
import com.nammashale.inventory.domain.usecase.UpdateAssetConditionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssetDetailUiState(
    val isDeleted: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AssetDetailViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val updateAssetConditionUseCase: UpdateAssetConditionUseCase,
    private val deleteAssetUseCase: DeleteAssetUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val assetId: Long = checkNotNull(savedStateHandle["assetId"])

    private val _uiState = MutableStateFlow(AssetDetailUiState())
    val uiState: StateFlow<AssetDetailUiState> = _uiState.asStateFlow()

    /** Asset exposed as a separate StateFlow for the Screen. */
    val asset: StateFlow<Asset?> = assetRepository.getAssetById(assetId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun updateCondition(condition: AssetCondition) {
        viewModelScope.launch {
            updateAssetConditionUseCase(assetId, condition)
            _uiState.value = _uiState.value.copy(successMessage = "Condition updated to ${condition.label}")
        }
    }

    fun deleteAsset() {
        viewModelScope.launch {
            val currentAsset = asset.value ?: return@launch
            deleteAssetUseCase(currentAsset)
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
