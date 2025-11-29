package com.rex50.tuneflow.domain.repository

import com.rex50.tuneflow.domain.model.Profile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Profile operations
 */
interface ProfileRepository {

    /**
     * Get all profiles as a Flow
     */
    fun getAllProfiles(): Flow<List<Profile>>

    /**
     * Get a specific profile by ID
     */
    suspend fun getProfileById(id: Long): Profile?

    /**
     * Get the currently selected profile
     */
    fun getSelectedProfile(): Flow<Profile?>

    /**
     * Save a new profile or update existing one
     * @return the ID of the saved profile
     */
    suspend fun saveProfile(profile: Profile): Long

    /**
     * Delete a profile
     */
    suspend fun deleteProfile(profile: Profile)

    /**
     * Select a profile (marks it as active)
     */
    suspend fun selectProfile(profileId: Long)

    /**
     * Check if a profile name already exists (excluding a specific ID)
     * @return true if name exists, false otherwise
     */
    suspend fun isProfileNameTaken(name: String, excludeId: Long = -1): Boolean

    /**
     * Ensure default profiles exist, re-seed if empty
     */
    suspend fun ensureDefaultProfiles()
}

