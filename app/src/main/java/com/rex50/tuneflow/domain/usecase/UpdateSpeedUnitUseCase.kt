package com.rex50.tuneflow.domain.usecase
import com.rex50.tuneflow.domain.model.SpeedUnit
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
class UpdateSpeedUnitUseCase(
    private val repository: VolumeSettingsRepository
) {
    suspend operator fun invoke(unit: SpeedUnit) = repository.updateSpeedUnit(unit)
}
