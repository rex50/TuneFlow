package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository

class UpdateServiceEnabledUseCase(
    private val repository: VolumeSettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.updateServiceEnabled(enabled)
}

