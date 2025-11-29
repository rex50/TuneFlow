package com.rex50.tuneflow.data.repository

import com.rex50.tuneflow.data.local.AppDatabase
import com.rex50.tuneflow.data.local.dao.ProfileDao
import com.rex50.tuneflow.data.local.entity.ProfileEntity
import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ProfileRepository using Room database
 */
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {

    override fun getAllProfiles(): Flow<List<Profile>> {
        return profileDao.getAllProfiles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProfileById(id: Long): Profile? {
        return profileDao.getProfileById(id)?.toDomain()
    }

    override fun getSelectedProfile(): Flow<Profile?> {
        return profileDao.getSelectedProfile().map { it?.toDomain() }
    }

    override suspend fun saveProfile(profile: Profile): Long {
        val entity = if (profile.id == 0L) {
            // New profile - check if it should be selected
            val count = profileDao.getProfileCount()
            ProfileEntity.fromDomain(profile, isSelected = count == 0)
        } else {
            // Updating existing - preserve selection state
            val existing = profileDao.getProfileById(profile.id)
            ProfileEntity.fromDomain(profile, isSelected = existing?.isSelected ?: false)
        }
        return profileDao.insertProfile(entity)
    }

    override suspend fun deleteProfile(profile: Profile) {
        val entity = ProfileEntity.fromDomain(profile)
        profileDao.deleteProfile(entity)
    }

    override suspend fun selectProfile(profileId: Long) {
        profileDao.selectProfileExclusively(profileId)
    }

    override suspend fun isProfileNameTaken(name: String, excludeId: Long): Boolean {
        return profileDao.getProfileByName(name, excludeId) != null
    }

    override suspend fun ensureDefaultProfiles() {
        AppDatabase.ensureDefaultProfiles(profileDao)
    }
}

