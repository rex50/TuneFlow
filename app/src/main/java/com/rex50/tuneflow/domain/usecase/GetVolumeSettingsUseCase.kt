package com.rex50.tuneflow.domain.usecase

import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository

class GetVolumeSettingsUseCase(
    private val repository: VolumeSettingsRepository
) {
    operator fun invoke() = repository.settings
}

