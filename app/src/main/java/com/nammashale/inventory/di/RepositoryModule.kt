package com.nammashale.inventory.di

import com.nammashale.inventory.data.repository.AiRepositoryImpl
import com.nammashale.inventory.data.repository.AssetRepositoryImpl
import com.nammashale.inventory.domain.repository.AiRepository
import com.nammashale.inventory.domain.repository.AssetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds interface types to their concrete implementations.
 * ViewModels and UseCases only depend on the interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAssetRepository(
        impl: AssetRepositoryImpl
    ): AssetRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        impl: AiRepositoryImpl
    ): AiRepository
}
