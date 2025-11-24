package com.rex50.tuneflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rex50.tuneflow.domain.model.AccelerationUnit
import com.rex50.tuneflow.domain.model.ServiceState
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.domain.usecase.GetVolumeSettingsUseCase
import com.rex50.tuneflow.domain.usecase.ObserveServiceStateUseCase
import com.rex50.tuneflow.domain.usecase.UpdateAccelerationRangeUseCase
import com.rex50.tuneflow.domain.usecase.UpdateAccelerationUnitUseCase
import com.rex50.tuneflow.domain.usecase.UpdateServiceEnabledUseCase
import com.rex50.tuneflow.domain.usecase.UpdateVolumeRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val updateVolumeRangeUseCase: UpdateVolumeRangeUseCase,
    private val updateAccelerationRangeUseCase: UpdateAccelerationRangeUseCase,
    private val updateServiceEnabledUseCase: UpdateServiceEnabledUseCase,
    private val updateAccelerationUnitUseCase: UpdateAccelerationUnitUseCase,
    observeServiceStateUseCase: ObserveServiceStateUseCase,
    getVolumeSettingsUseCase: GetVolumeSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceState(speed = 0f, volume = 0))
    val state: StateFlow<ServiceState> = _state

    private val _volumeSettings = MutableStateFlow(VolumeSettings())
    val volumeSettings: StateFlow<VolumeSettings> = _volumeSettings

    init {
        observeServiceStateUseCase().onEach { serviceState ->
            _state.value = serviceState
        }.launchIn(viewModelScope)

        // Observe datastore settings and keep a StateFlow for UI consumption
        getVolumeSettingsUseCase().onEach { settings ->
            _volumeSettings.value = settings
        }.launchIn(viewModelScope)
    }

    fun updateMinVolume(volume: Int) {
        viewModelScope.launch { updateVolumeRangeUseCase.updateMin(volume) }
    }

    fun updateMaxVolume(volume: Int) {
        viewModelScope.launch { updateVolumeRangeUseCase.updateMax(volume) }
    }

    fun updateMinAcceleration(value: Float) {
        viewModelScope.launch { updateAccelerationRangeUseCase.updateMin(value) }
    }

    fun updateMaxAcceleration(value: Float) {
        viewModelScope.launch { updateAccelerationRangeUseCase.updateMax(value) }
    }

    fun setServiceEnabled(enabled: Boolean) {
        viewModelScope.launch { updateServiceEnabledUseCase(enabled) }
    }

    fun updateAccelerationUnit(unit: AccelerationUnit) {
        viewModelScope.launch { updateAccelerationUnitUseCase(unit) }
    }
}
