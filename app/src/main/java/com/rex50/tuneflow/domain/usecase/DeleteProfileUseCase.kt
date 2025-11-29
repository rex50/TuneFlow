package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Use case to delete a profile and ensure defaults are re-seeded if needed
 */
class DeleteProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile): Boolean {
        profileRepository.deleteProfile(profile)

        // Ensure default profiles exist after deletion
        profileRepository.ensureDefaultProfiles()

        return true
    }
}

