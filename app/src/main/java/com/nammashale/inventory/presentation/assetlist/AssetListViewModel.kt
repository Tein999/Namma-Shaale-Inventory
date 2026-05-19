package com.nammashale.inventory.presentation.assetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.domain.usecase.DeleteAssetUseCase
import com.nammashale.inventory.domain.usecase.SearchAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AssetListViewModel @Inject constructor(
    private val searchAssetsUseCase: SearchAssetsUseCase,
    private val deleteAssetUseCase: DeleteAssetUseCase
) : ViewModel() {

    // Exposed individually so Screen can collect each separately
    val searchQuery: StateFlow<String>
    val conditionFilter: StateFlow<AssetCondition?>
    val assets: StateFlow<List<Asset>>
    val snackbarMessage: StateFlow<String?>

    private val _searchQuery = MutableStateFlow("")
    private val _conditionFilter = MutableStateFlow<AssetCondition?>(null)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    init {
        searchQuery = _searchQuery.asStateFlow()
        conditionFilter = _conditionFilter.asStateFlow()
        snackbarMessage = _snackbarMessage.asStateFlow()

        assets = _searchQuery
            .debounce(300L)
            .combine(_conditionFilter) { query, filter -> Pair(query, filter) }
            .flatMapLatest { (query, filter) ->
                searchAssetsUseCase(query, filter)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onConditionFilterChange(condition: AssetCondition?) {
        _conditionFilter.value = condition
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch {
            deleteAssetUseCase(asset)
            _snackbarMessage.value = "${asset.name} deleted."
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
