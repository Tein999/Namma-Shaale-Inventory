package com.nammashale.inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nammashale.inventory.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the assets table.
 * All read operations return Flow for reactive updates.
 * All write operations are suspend functions for coroutine compatibility.
 */
@Dao
interface AssetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)

    @Query("SELECT * FROM assets ORDER BY date_added DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    fun getAssetById(id: Long): Flow<AssetEntity?>

    @Query("""
        SELECT * FROM assets 
        WHERE name LIKE '%' || :query || '%' 
        OR serial_number LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        OR location LIKE '%' || :query || '%'
        ORDER BY date_added DESC
    """)
    fun searchAssets(query: String): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE `condition` = :condition ORDER BY date_added DESC")
    fun getAssetsByCondition(condition: String): Flow<List<AssetEntity>>

    @Query("UPDATE assets SET `condition` = :condition WHERE id = :id")
    suspend fun updateAssetCondition(id: Long, condition: String)

    @Query("UPDATE assets SET last_health_check = :timestamp WHERE id = :id")
    suspend fun updateHealthCheck(id: Long, timestamp: Long)

    // Aggregate queries for dashboard
    @Query("SELECT COUNT(*) FROM assets")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM assets WHERE `condition` = :condition")
    fun getCountByCondition(condition: String): Flow<Int>
}
