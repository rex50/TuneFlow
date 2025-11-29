package com.rex50.tuneflow.di

import android.content.Context
import com.rex50.tuneflow.data.PreferencesManager
import com.rex50.tuneflow.data.local.AppDatabase
import com.rex50.tuneflow.data.local.dao.ProfileDao
import com.rex50.tuneflow.data.repository.InMemoryServiceStateRepository
import com.rex50.tuneflow.data.repository.PermissionStatusRepositoryImpl
import com.rex50.tuneflow.data.repository.ProfileRepositoryImpl
import com.rex50.tuneflow.domain.repository.PermissionStatusRepository
import com.rex50.tuneflow.domain.repository.ProfileRepository
import com.rex50.tuneflow.domain.repository.ServiceStateRepository
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import com.rex50.tuneflow.domain.usecase.*
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

    // Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideProfileDao(database: AppDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileDao: ProfileDao): ProfileRepository {
        return ProfileRepositoryImpl(profileDao)
    }

    // Provide domain use-cases
    @Provides
    @Singleton
    fun provideGetVolumeSettingsUseCase(
        repository: VolumeSettingsRepository
    ): ObserveVolumeSettingsUseCase = ObserveVolumeSettingsUseCase(repository)

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

    // Profile use cases
    @Provides
    @Singleton
    fun provideGetAllProfilesUseCase(
        repository: ProfileRepository
    ): GetAllProfilesUseCase = GetAllProfilesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSelectedProfileUseCase(
        repository: ProfileRepository
    ): GetSelectedProfileUseCase = GetSelectedProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideSelectProfileUseCase(
        profileRepository: ProfileRepository,
        volumeSettingsRepository: VolumeSettingsRepository
    ): SelectProfileUseCase = SelectProfileUseCase(profileRepository, volumeSettingsRepository)

    @Provides
    @Singleton
    fun provideSaveProfileUseCase(
        repository: ProfileRepository
    ): SaveProfileUseCase = SaveProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteProfileUseCase(
        repository: ProfileRepository
    ): DeleteProfileUseCase = DeleteProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideValidateProfileNameUseCase(
        repository: ProfileRepository
    ): ValidateProfileNameUseCase = ValidateProfileNameUseCase(repository)
}
