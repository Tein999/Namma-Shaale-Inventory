package com.nammashale.inventory.presentation.issuelog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.domain.model.IssueLog
import com.nammashale.inventory.domain.usecase.GetAllIssueLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllIssueLogsViewModel @Inject constructor(
    private val getAllIssueLogsUseCase: GetAllIssueLogsUseCase
) : ViewModel() {

    /** Directly exposed as StateFlow for easy collection in the Screen. */
    val allIssues: StateFlow<List<IssueLog>> = getAllIssueLogsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
