package com.nammashale.inventory.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nammashale.inventory.domain.model.IssueLog

/**
 * Room entity for issue logs.
 * Has a foreign key relationship with AssetEntity.
 * Cascade delete ensures issue logs are removed when their parent asset is deleted.
 */
@Entity(
    tableName = "issue_logs",
    foreignKeys = [
        ForeignKey(
            entity = AssetEntity::class,
            parentColumns = ["id"],
            childColumns = ["asset_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["asset_id"])]
)
data class IssueLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "asset_id")
    val assetId: Long,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "ai_suggestion")
    val aiSuggestion: String = "",

    @ColumnInfo(name = "date_logged")
    val dateLogged: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_resolved")
    val isResolved: Boolean = false
) {
    fun toDomain(): IssueLog = IssueLog(
        id = id,
        assetId = assetId,
        description = description,
        aiSuggestion = aiSuggestion,
        dateLogged = dateLogged,
        isResolved = isResolved
    )

    companion object {
        fun fromDomain(issueLog: IssueLog): IssueLogEntity = IssueLogEntity(
            id = issueLog.id,
            assetId = issueLog.assetId,
            description = issueLog.description,
            aiSuggestion = issueLog.aiSuggestion,
            dateLogged = issueLog.dateLogged,
            isResolved = issueLog.isResolved
        )
    }
}
