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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * Consolidated UI state for the Home screen
 */
data class HomeScreenUiState(
    val serviceState: ServiceState = ServiceState(speed = 0f, volume = 0),
    val isServiceEnabled: Boolean = false,
    val permissionsUiState: PermissionsUiState = PermissionsUiState.AllGranted,
    val profiles: List<Profile> = emptyList(),
    val selectedProfile: Profile? = null
)
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val permissionStatusRepository: PermissionStatusRepository,
    getAllProfilesUseCase: GetAllProfilesUseCase,
    getSelectedProfileUseCase: GetSelectedProfileUseCase,
    private val selectProfileUseCase: SelectProfileUseCase,
    serviceRepository: ServiceStateRepository,
    observePermissionsUseCase: ObservePermissionsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()
    init {
        // Observe service state and enabled status
        serviceRepository.dataUpdates.onEach { serviceState ->
            _uiState.update { it.copy(serviceState = serviceState) }
        }.launchIn(viewModelScope)
        serviceRepository.isServiceEnabled.onEach { isEnabled ->
            _uiState.update { it.copy(isServiceEnabled = isEnabled) }
        }.launchIn(viewModelScope)
        // Observe permission status
        observePermissionsUseCase().onEach { permissionState ->
            _uiState.update { it.copy(permissionsUiState = permissionState) }
        }.launchIn(viewModelScope)
        // Observe all profiles
        getAllProfilesUseCase().onEach { profiles ->
            _uiState.update { it.copy(profiles = profiles) }
        }.launchIn(viewModelScope)
        // Observe selected profile
        getSelectedProfileUseCase().onEach { profile ->
            _uiState.update { it.copy(selectedProfile = profile) }
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
                    val currentState = _uiState.value.permissionsUiState
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