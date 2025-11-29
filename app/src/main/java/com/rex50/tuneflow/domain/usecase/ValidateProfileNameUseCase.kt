package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.ProfileRepository
import javax.inject.Inject

/**
 * Use case to validate profile name
 */
class ValidateProfileNameUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    /**
     * Validates a profile name
     * @param name The name to validate
     * @param excludeId Profile ID to exclude from uniqueness check (for updates)
     * @return ValidationResult indicating if valid and error message if not
     */
    suspend operator fun invoke(name: String, excludeId: Long = -1): ValidationResult {
        // Check if empty
        if (name.isBlank()) {
            return ValidationResult(false, "Profile name cannot be empty")
        }

        // Check length
        if (name.length > 20) {
            return ValidationResult(false, "Profile name must be 20 characters or less")
        }

        // Check uniqueness
        if (profileRepository.isProfileNameTaken(name, excludeId)) {
            return ValidationResult(false, "Profile name already exists")
        }

        return ValidationResult(true, null)
    }

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String?
    )
}

