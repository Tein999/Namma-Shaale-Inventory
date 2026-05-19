package com.nammashale.inventory.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition

/**
 * Room database entity for assets.
 * Uses an index on serialNumber to speed up lookups.
 */
@Entity(
    tableName = "assets",
    indices = [Index(value = ["serial_number"])]
)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "serial_number")
    val serialNumber: String,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String? = null,

    @ColumnInfo(name = "condition")
    val condition: String = AssetCondition.WORKING.name,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "date_added")
    val dateAdded: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_health_check")
    val lastHealthCheck: Long? = null,

    @ColumnInfo(name = "location")
    val location: String = ""
) {
    /**
     * Maps this DB entity to the domain model.
     */
    fun toDomain(): Asset = Asset(
        id = id,
        name = name,
        serialNumber = serialNumber,
        photoUri = photoUri,
        condition = AssetCondition.valueOf(condition),
        description = description,
        dateAdded = dateAdded,
        lastHealthCheck = lastHealthCheck,
        location = location
    )

    companion object {
        /**
         * Maps a domain Asset to a Room entity.
         */
        fun fromDomain(asset: Asset): AssetEntity = AssetEntity(
            id = asset.id,
            name = asset.name,
            serialNumber = asset.serialNumber,
            photoUri = asset.photoUri,
            condition = asset.condition.name,
            description = asset.description,
            dateAdded = asset.dateAdded,
            lastHealthCheck = asset.lastHealthCheck,
            location = asset.location
        )
    }
}
