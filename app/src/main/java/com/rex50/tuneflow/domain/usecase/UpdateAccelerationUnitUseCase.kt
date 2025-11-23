package com.rex50.tuneflow.domain.usecase
import com.rex50.tuneflow.domain.model.AccelerationUnit
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
class UpdateAccelerationUnitUseCase(
    private val repository: VolumeSettingsRepository
) {
    suspend operator fun invoke(unit: AccelerationUnit) = repository.updateAccelerationUnit(unit)
}
