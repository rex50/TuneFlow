package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Use case to save a profile (create new or update existing)
 */
class SaveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile): Long {
        return profileRepository.saveProfile(profile)
    }
}

