package com.nammashale.inventory.domain.usecase

import com.nammashale.inventory.domain.model.IssueLog
import com.nammashale.inventory.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddIssueLogUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    suspend operator fun invoke(issueLog: IssueLog): Result<Long> {
        if (issueLog.description.isBlank()) {
            return Result.failure(IllegalArgumentException("Issue description cannot be empty"))
        }
        return try {
            val id = repository.insertIssueLog(issueLog)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetIssueLogsForAssetUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    operator fun invoke(assetId: Long): Flow<List<IssueLog>> =
        repository.getIssueLogsForAsset(assetId)
}

class GetAllIssueLogsUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    operator fun invoke(): Flow<List<IssueLog>> = repository.getAllIssueLogs()
}

class MarkIssueResolvedUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    suspend operator fun invoke(issueId: Long) {
        repository.markIssueResolved(issueId)
    }
}
