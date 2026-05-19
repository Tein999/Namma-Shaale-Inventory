package com.nammashale.inventory.presentation.issuelog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.domain.model.IssueLog
import com.nammashale.inventory.domain.repository.AiRepository
import com.nammashale.inventory.domain.repository.AssetRepository
import com.nammashale.inventory.domain.usecase.AddIssueLogUseCase
import com.nammashale.inventory.domain.usecase.GetIssueLogsForAssetUseCase
import com.nammashale.inventory.domain.usecase.MarkIssueResolvedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssueLogUiState(
    val assetName: String = "",
    val assetCondition: String = "",
    val descriptionInput: String = "",
    val aiSuggestion: String = "",
    val isGeneratingAi: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class IssueLogViewModel @Inject constructor(
    private val addIssueLogUseCase: AddIssueLogUseCase,
    private val getIssueLogsForAssetUseCase: GetIssueLogsForAssetUseCase,
    private val markIssueResolvedUseCase: MarkIssueResolvedUseCase,
    private val aiRepository: AiRepository,
    private val assetRepository: AssetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val assetId: Long = checkNotNull(savedStateHandle["assetId"])

    private val _uiState = MutableStateFlow(IssueLogUiState())
    val uiState: StateFlow<IssueLogUiState> = _uiState.asStateFlow()

    /** Issue list exposed as separate StateFlow for cleaner collection in the Screen. */
    val issues: StateFlow<List<IssueLog>> = getIssueLogsForAssetUseCase(assetId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        loadAssetInfo()
    }

    private fun loadAssetInfo() {
        viewModelScope.launch {
            assetRepository.getAssetById(assetId).collect { asset ->
                if (asset != null) {
                    _uiState.value = _uiState.value.copy(
                        assetName = asset.name,
                        assetCondition = asset.condition.label
                    )
                }
            }
        }
    }

    fun onDescriptionChange(text: String) {
        _uiState.value = _uiState.value.copy(descriptionInput = text)
    }

    fun generateAiDescription() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingAi = true)
            aiRepository.generateIssueDescription(
                assetName = state.assetName,
                condition = state.assetCondition
            ).onSuccess { suggestion ->
                _uiState.value = _uiState.value.copy(
                    descriptionInput = suggestion,
                    isGeneratingAi = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isGeneratingAi = false,
                    errorMessage = "AI generation failed. Please type manually."
                )
            }
        }
    }

    fun addIssue() {
        val state = _uiState.value
        if (state.descriptionInput.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please describe the issue.")
            return
        }
        viewModelScope.launch {
            val issueLog = IssueLog(
                assetId = assetId,
                description = state.descriptionInput.trim(),
                aiSuggestion = state.aiSuggestion,
                dateLogged = System.currentTimeMillis()
            )
            addIssueLogUseCase(issueLog)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        descriptionInput = "",
                        aiSuggestion = "",
                        successMessage = "Issue logged successfully!"
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(errorMessage = it.message)
                }
        }
    }

    fun markResolved(issueId: Long) {
        viewModelScope.launch {
            markIssueResolvedUseCase(issueId)
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
