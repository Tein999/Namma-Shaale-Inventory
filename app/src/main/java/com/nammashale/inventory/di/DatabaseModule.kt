package com.nammashale.inventory.di

import android.content.Context
import androidx.room.Room
import com.nammashale.inventory.data.local.AppDatabase
import com.nammashale.inventory.data.local.dao.AssetDao
import com.nammashale.inventory.data.local.dao.IssueLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides all database-related dependencies.
 * Installed in the SingletonComponent = app-wide singleton lifetime.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            // In production, use proper Migrations instead of this:
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAssetDao(database: AppDatabase): AssetDao = database.assetDao()

    @Provides
    @Singleton
    fun provideIssueLogDao(database: AppDatabase): IssueLogDao = database.issueLogDao()
}
