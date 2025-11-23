package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository

class UpdateAccelerationRangeUseCase(
    private val repository: VolumeSettingsRepository
) {
    suspend fun updateMin(value: Float) = repository.updateMinAcceleration(value)
    suspend fun updateMax(value: Float) = repository.updateMaxAcceleration(value)
}

