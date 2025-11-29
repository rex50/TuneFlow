package com.rex50.tuneflow.di

import android.content.Context
import com.rex50.tuneflow.data.PreferencesManager
import com.rex50.tuneflow.data.repository.InMemoryServiceStateRepository
import com.rex50.tuneflow.data.repository.PermissionStatusRepositoryImpl
import com.rex50.tuneflow.domain.repository.PermissionStatusRepository
import com.rex50.tuneflow.domain.repository.ServiceStateRepository
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import com.rex50.tuneflow.domain.usecase.GetVolumeSettingsUseCase
import com.rex50.tuneflow.domain.usecase.ObservePermissionsUseCase
import com.rex50.tuneflow.domain.usecase.ObserveServiceStateUseCase
import com.rex50.tuneflow.domain.usecase.UpdateSpeedRangeUseCase
import com.rex50.tuneflow.domain.usecase.UpdateSpeedUnitUseCase
import com.rex50.tuneflow.domain.usecase.UpdateServiceEnabledUseCase
import com.rex50.tuneflow.domain.usecase.UpdateVolumeRangeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVolumeSettingsRepository(
        @ApplicationContext context: Context
    ): VolumeSettingsRepository = PreferencesManager(context)

    @Provides
    @Singleton
    fun provideServiceStateRepository(): ServiceStateRepository = InMemoryServiceStateRepository()

    // Provide domain use-cases
    @Provides
    @Singleton
    fun provideGetVolumeSettingsUseCase(
        repository: VolumeSettingsRepository
    ): GetVolumeSettingsUseCase = GetVolumeSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateVolumeRangeUseCase(
        repository: VolumeSettingsRepository
    ): UpdateVolumeRangeUseCase = UpdateVolumeRangeUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateSpeedRangeUseCase(
        repository: VolumeSettingsRepository
    ): UpdateSpeedRangeUseCase = UpdateSpeedRangeUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateServiceEnabledUseCase(
        repository: VolumeSettingsRepository
    ): UpdateServiceEnabledUseCase = UpdateServiceEnabledUseCase(repository)

    @Provides
    @Singleton
    fun provideObserveServiceStateUseCase(
        repository: ServiceStateRepository
    ): ObserveServiceStateUseCase = ObserveServiceStateUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateSpeedUnitUseCase(
        repository: VolumeSettingsRepository
    ): UpdateSpeedUnitUseCase = UpdateSpeedUnitUseCase(repository)

    @Provides
    @Singleton
    fun providePermissionStatusRepository(
        @ApplicationContext context: Context
    ): PermissionStatusRepository = PermissionStatusRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideObservePermissionsUseCase(
        repository: PermissionStatusRepository
    ): ObservePermissionsUseCase = ObservePermissionsUseCase(repository)
}
