package com.rex50.tuneflow.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rex50.tuneflow.domain.model.PermissionEvent
import com.rex50.tuneflow.domain.model.PermissionType
import com.rex50.tuneflow.domain.model.PermissionsUiState
import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.model.ServiceState
import com.rex50.tuneflow.domain.repository.PermissionStatusRepository
import com.rex50.tuneflow.domain.repository.ServiceStateRepository
import com.rex50.tuneflow.domain.usecase.GetAllProfilesUseCase
import com.rex50.tuneflow.domain.usecase.GetSelectedProfileUseCase
import com.rex50.tuneflow.domain.usecase.ObservePermissionsUseCase
import com.rex50.tuneflow.domain.usecase.SelectProfileUseCase
import com.rex50.tuneflow.service.VolumeControlService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val permissionStatusRepository: PermissionStatusRepository,
    getAllProfilesUseCase: GetAllProfilesUseCase,
    getSelectedProfileUseCase: GetSelectedProfileUseCase,
    private val selectProfileUseCase: SelectProfileUseCase,
    private val serviceRepository: ServiceStateRepository,
    observePermissionsUseCase: ObservePermissionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceState(speed = 0f, volume = 0))
    val state: StateFlow<ServiceState> = _state

    val isServiceEnabled: StateFlow<Boolean> = serviceRepository.isServiceEnabled

    private val _permissionsUiState =
        MutableStateFlow<PermissionsUiState>(PermissionsUiState.AllGranted)
    val permissionsUiState: StateFlow<PermissionsUiState> = _permissionsUiState.asStateFlow()

    private val _profiles =
        MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _selectedProfile = MutableStateFlow<Profile?>(null)
    val selectedProfile: StateFlow<Profile?> =
        _selectedProfile.asStateFlow()

    init {
        serviceRepository.dataUpdates.onEach { serviceState ->
            _state.value = serviceState
        }.launchIn(viewModelScope)

        // Observe permission status
        observePermissionsUseCase().onEach { permissionState ->
            _permissionsUiState.value = permissionState
        }.launchIn(viewModelScope)

        // Observe all profiles
        getAllProfilesUseCase().onEach { profiles ->
            _profiles.value = profiles
        }.launchIn(viewModelScope)

        // Observe selected profile
        getSelectedProfileUseCase().onEach { profile ->
            _selectedProfile.value = profile
        }.launchIn(viewModelScope)
    }

    fun selectProfile(profileId: Long) {
        viewModelScope.launch {
            selectProfileUseCase(profileId)
        }
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
