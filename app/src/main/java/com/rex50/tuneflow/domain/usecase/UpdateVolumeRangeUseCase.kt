package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository

class UpdateVolumeRangeUseCase(
    private val repository: VolumeSettingsRepository
) {
    suspend fun updateMin(volume: Int) = repository.updateMinVolume(volume)
    suspend fun updateMax(volume: Int) = repository.updateMaxVolume(volume)
}

