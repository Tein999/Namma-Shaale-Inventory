package com.nammashale.inventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nammashale.inventory.data.local.dao.AssetDao
import com.nammashale.inventory.data.local.dao.IssueLogDao
import com.nammashale.inventory.data.local.entity.AssetEntity
import com.nammashale.inventory.data.local.entity.IssueLogEntity

/**
 * The single Room database instance for the entire app.
 *
 * Version history:
 *  v1 → Initial schema (assets + issue_logs tables)
 *
 * NOTE: When adding columns in future versions, create a Migration object
 * instead of using fallbackToDestructiveMigration() to preserve user data.
 */
@Database(
    entities = [
        AssetEntity::class,
        IssueLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun assetDao(): AssetDao
    abstract fun issueLogDao(): IssueLogDao

    companion object {
        const val DATABASE_NAME = "namma_shaale_db"
    }
}
