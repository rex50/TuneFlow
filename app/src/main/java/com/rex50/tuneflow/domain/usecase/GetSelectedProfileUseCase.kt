package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get the currently selected profile
 */
class GetSelectedProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<Profile?> {
        return profileRepository.getSelectedProfile()
    }
}

