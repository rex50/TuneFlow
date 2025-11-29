package com.rex50.tuneflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.model.SpeedUnit

/**
 * Room entity for storing profile data
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val minSpeed: Float,
    val maxSpeed: Float,
    val minVolumePercent: Int,
    val maxVolumePercent: Int,
    val speedUnit: String,
    val isSelected: Boolean = false
) {
    fun toDomain(): Profile = Profile(
        id = id,
        name = name,
        colorHex = colorHex,
        minSpeed = minSpeed,
        maxSpeed = maxSpeed,
        minVolumePercent = minVolumePercent,
        maxVolumePercent = maxVolumePercent,
        speedUnit = SpeedUnit.valueOf(speedUnit)
    )

    companion object {
        fun fromDomain(profile: Profile, isSelected: Boolean = false): ProfileEntity = ProfileEntity(
            id = profile.id,
            name = profile.name,
            colorHex = profile.colorHex,
            minSpeed = profile.minSpeed,
            maxSpeed = profile.maxSpeed,
            minVolumePercent = profile.minVolumePercent,
            maxVolumePercent = profile.maxVolumePercent,
            speedUnit = profile.speedUnit.name,
            isSelected = isSelected
        )
    }
}

