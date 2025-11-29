package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository

class ObserveVolumeSettingsUseCase(
    private val repository: VolumeSettingsRepository
) {
    operator fun invoke() = repository.settings
}

