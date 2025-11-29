package com.rex50.tuneflow.domain.repository

import com.rex50.tuneflow.domain.model.SpeedUnit
import com.rex50.tuneflow.domain.model.VolumeSettings
import kotlinx.coroutines.flow.Flow

interface VolumeSettingsRepository {
    val settings: Flow<VolumeSettings>
    suspend fun updateMinVolume(volume: Int)
    suspend fun updateMaxVolume(volume: Int)
    suspend fun updateMinSpeed(value: Float)
    suspend fun updateMaxSpeed(value: Float)
    suspend fun updateServiceEnabled(enabled: Boolean)
    suspend fun updateSpeedUnit(unit: SpeedUnit)
}

