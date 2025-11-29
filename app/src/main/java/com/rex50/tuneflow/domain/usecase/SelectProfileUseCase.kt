package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.repository.ProfileRepository
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import javax.inject.Inject

/**
 * Use case to select a profile and apply its settings
 */
class SelectProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val volumeSettingsRepository: VolumeSettingsRepository
) {
    suspend operator fun invoke(profileId: Long) {
        // Mark profile as selected
        profileRepository.selectProfile(profileId)

        // Get the profile and apply its settings
        val profile = profileRepository.getProfileById(profileId)
        profile?.let {
            // Update volume settings to match the profile
            volumeSettingsRepository.updateMinVolume(it.minVolumePercent)
            volumeSettingsRepository.updateMaxVolume(it.maxVolumePercent)
            volumeSettingsRepository.updateMinSpeed(it.minSpeed)
            volumeSettingsRepository.updateMaxSpeed(it.maxSpeed)
            volumeSettingsRepository.updateSpeedUnit(it.speedUnit)
        }
    }
}

