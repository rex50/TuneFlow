package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository

class UpdateSpeedRangeUseCase(
    private val repository: VolumeSettingsRepository
) {
    suspend fun updateMin(value: Float) = repository.updateMinSpeed(value)
    suspend fun updateMax(value: Float) = repository.updateMaxSpeed(value)
}

