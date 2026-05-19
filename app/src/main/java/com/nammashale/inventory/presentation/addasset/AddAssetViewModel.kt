package com.nammashale.inventory.presentation.addasset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.usecase.AddAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddAssetUiState(
    val name: String = "",
    val serialNumber: String = "",
    val location: String = "",
    val description: String = "",
    val condition: AssetCondition = AssetCondition.WORKING,
    val photoPath: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val nameError: String? = null,
    val serialError: String? = null,
    /** Shown in Snackbar. Aliased as errorMessage for the Screen. */
    val errorMessage: String? = null
)

@HiltViewModel
class AddAssetViewModel @Inject constructor(
    private val addAssetUseCase: AddAssetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAssetUiState())
    val uiState: StateFlow<AddAssetUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null)
    }

    fun onSerialNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(serialNumber = value, serialError = null)
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onConditionChange(condition: AssetCondition) {
        _uiState.value = _uiState.value.copy(condition = condition)
    }

    /** Called when CameraScreen returns a captured photo path. */
    fun onPhotoTaken(path: String?) {
        _uiState.value = _uiState.value.copy(photoPath = path)
    }

    /** Legacy alias kept for backward compatibility */
    fun onPhotoPathReceived(path: String?) = onPhotoTaken(path)

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun saveAsset() {
        val state = _uiState.value

        // Client-side validation
        var hasError = false
        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "Name is required")
            hasError = true
        }
        if (state.serialNumber.isBlank()) {
            _uiState.value = _uiState.value.copy(serialError = "Serial number is required")
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val asset = Asset(
                name = state.name.trim(),
                serialNumber = state.serialNumber.trim(),
                location = state.location.trim(),
                description = state.description.trim(),
                condition = state.condition,
                photoUri = state.photoPath,
                dateAdded = System.currentTimeMillis()
            )

            addAssetUseCase(asset).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSaved = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to save asset"
                    )
                }
            )
        }
    }
}
