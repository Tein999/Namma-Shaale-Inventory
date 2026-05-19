package com.nammashale.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nammashale.inventory.data.local.entity.IssueLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the issue_logs table.
 */
@Dao
interface IssueLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssueLog(issueLog: IssueLogEntity): Long

    @Delete
    suspend fun deleteIssueLog(issueLog: IssueLogEntity)

    @Query("SELECT * FROM issue_logs WHERE asset_id = :assetId ORDER BY date_logged DESC")
    fun getIssueLogsForAsset(assetId: Long): Flow<List<IssueLogEntity>>

    @Query("SELECT * FROM issue_logs ORDER BY date_logged DESC")
    fun getAllIssueLogs(): Flow<List<IssueLogEntity>>

    @Query("UPDATE issue_logs SET is_resolved = 1 WHERE id = :id")
    suspend fun markIssueResolved(id: Long)

    @Query("SELECT COUNT(*) FROM issue_logs WHERE asset_id = :assetId AND is_resolved = 0")
    fun getOpenIssueCountForAsset(assetId: Long): Flow<Int>
}
