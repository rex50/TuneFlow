package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Use case to select a profile
 * Settings are automatically applied via VolumeSettingsRepository
 */
class SelectProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profileId: Long) {
        // Mark profile as selected
        // VolumeSettingsRepository will automatically emit the new profile's settings
        profileRepository.selectProfile(profileId)
    }
}
