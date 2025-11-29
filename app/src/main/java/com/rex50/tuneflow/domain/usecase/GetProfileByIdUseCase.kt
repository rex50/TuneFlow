package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Use case to get a specific profile by its ID
 */
class GetProfileByIdUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profileId: Long): Profile? {
        return profileRepository.getProfileById(profileId)
    }
}

