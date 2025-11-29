package com.rex50.tuneflow.data.local.dao

import androidx.room.*
import com.rex50.tuneflow.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Profile operations
 */
@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE isSelected = 1 LIMIT 1")
    fun getSelectedProfile(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE name = :name AND id != :excludeId")
    suspend fun getProfileByName(name: String, excludeId: Long = -1): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("UPDATE profiles SET isSelected = 0")
    suspend fun clearAllSelections()

    @Query("UPDATE profiles SET isSelected = 1 WHERE id = :profileId")
    suspend fun selectProfile(profileId: Long)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getProfileCount(): Int

    @Transaction
    suspend fun selectProfileExclusively(profileId: Long) {
        clearAllSelections()
        selectProfile(profileId)
    }
}

