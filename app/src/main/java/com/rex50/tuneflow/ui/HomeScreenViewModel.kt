package com.rex50.tuneflow.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rex50.tuneflow.domain.model.AccelerationUnit
import com.rex50.tuneflow.domain.model.PermissionType
import com.rex50.tuneflow.domain.model.PermissionsUiState
import com.rex50.tuneflow.domain.model.ServiceState
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.domain.repository.PermissionStatusRepository
import com.rex50.tuneflow.domain.usecase.GetVolumeSettingsUseCase
import com.rex50.tuneflow.domain.usecase.ObservePermissionsUseCase
import com.rex50.tuneflow.domain.usecase.ObserveServiceStateUseCase
import com.rex50.tuneflow.domain.usecase.UpdateAccelerationRangeUseCase
import com.rex50.tuneflow.domain.usecase.UpdateAccelerationUnitUseCase
import com.rex50.tuneflow.domain.usecase.UpdateServiceEnabledUseCase
import com.rex50.tuneflow.domain.usecase.UpdateVolumeRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Events related to permission handling
 */
sealed class PermissionEvent {
    data class RequestPermissions(val permissions: List<String>) : PermissionEvent()
    data object RequestGpsEnable : PermissionEvent()
    data object OpenSettings : PermissionEvent()
    data object GpsResolutionFailed : PermissionEvent()
    data object RequestBatteryOptimization : PermissionEvent()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val updateVolumeRangeUseCase: UpdateVolumeRangeUseCase,
    private val updateAccelerationRangeUseCase: UpdateAccelerationRangeUseCase,
    private val updateServiceEnabledUseCase: UpdateServiceEnabledUseCase,
    private val updateAccelerationUnitUseCase: UpdateAccelerationUnitUseCase,
    private val permissionStatusRepository: PermissionStatusRepository,
    observeServiceStateUseCase: ObserveServiceStateUseCase,
    observePermissionsUseCase: ObservePermissionsUseCase,
    getVolumeSettingsUseCase: GetVolumeSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceState(speed = 0f, volume = 0))
    val state: StateFlow<ServiceState> = _state

    private val _volumeSettings = MutableStateFlow(VolumeSettings())
    val volumeSettings: StateFlow<VolumeSettings> = _volumeSettings

    private val _permissionsUiState =
        MutableStateFlow<PermissionsUiState>(PermissionsUiState.AllGranted)
    val permissionsUiState: StateFlow<PermissionsUiState> = _permissionsUiState.asStateFlow()

    init {
        observeServiceStateUseCase().onEach { serviceState ->
            _state.value = serviceState
        }.launchIn(viewModelScope)

        // Observe datastore settings and keep a StateFlow for UI consumption
        getVolumeSettingsUseCase().onEach { settings ->
            _volumeSettings.value = settings
        }.launchIn(viewModelScope)

        // Observe permission status
        observePermissionsUseCase().onEach { permissionState ->
            _permissionsUiState.value = permissionState
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

    @SuppressLint("InlinedApi")
    fun onPermissionAction(permissionType: PermissionType) {
        viewModelScope.launch {
            when (permissionType) {
                PermissionType.FINE_LOCATION,
                PermissionType.COARSE_LOCATION,
                PermissionType.POST_NOTIFICATIONS -> {
                    // Collect required permissions
                    val permissions = mutableListOf<String>()
                    val currentState = _permissionsUiState.value
                    if (currentState is PermissionsUiState.MissingRequirements) {
                        currentState.requirements.forEach { status ->
                            when (status.type) {
                                PermissionType.FINE_LOCATION -> if (!status.isGranted) {
                                    if (status.canRequest) {
                                        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    } else {
                                        permissionStatusRepository.handleEvent(PermissionEvent.OpenSettings)
                                        return@launch
                                    }
                                }

                                PermissionType.COARSE_LOCATION -> if (!status.isGranted) {
                                    if (status.canRequest) {
                                        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                    } else {
                                        permissionStatusRepository.handleEvent(PermissionEvent.OpenSettings)
                                        return@launch
                                    }
                                }

                                PermissionType.POST_NOTIFICATIONS -> if (!status.isGranted) {
                                    if (status.canRequest) {
                                        permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        permissionStatusRepository.handleEvent(PermissionEvent.OpenSettings)
                                        return@launch
                                    }
                                }

                                else -> {}
                            }
                        }
                        if (permissions.isNotEmpty()) {
                            permissionStatusRepository.handleEvent(
                                PermissionEvent.RequestPermissions(
                                    permissions
                                )
                            )
                        }
                    }
                }

                PermissionType.GPS_ENABLED -> {
                    permissionStatusRepository.handleEvent(PermissionEvent.RequestGpsEnable)
                }

                PermissionType.BATTERY_OPTIMIZATION -> {
                    permissionStatusRepository.handleEvent(PermissionEvent.RequestBatteryOptimization)
                }
            }
        }
    }
}
