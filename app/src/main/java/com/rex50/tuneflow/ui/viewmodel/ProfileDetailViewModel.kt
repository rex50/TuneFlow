package com.rex50.tuneflow.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.domain.model.SpeedUnit
import com.rex50.tuneflow.domain.usecase.*
import com.rex50.tuneflow.ui.navigation.ProfileDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileDetailUiState(
    val profile: Profile? = null,
    val nameError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val isNewProfile: Boolean = true
)

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val validateProfileNameUseCase: ValidateProfileNameUseCase,
    private val getProfileByIdUseCase: GetProfileByIdUseCase
) : ViewModel() {

    private val profileId: Long = savedStateHandle.toRoute<ProfileDetail>().profileId

    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState: StateFlow<ProfileDetailUiState> = _uiState.asStateFlow()

    init {
        if (profileId > 0) {
            // Editing existing profile
            loadProfile(profileId)
        } else {
            // Creating new profile with default values
            _uiState.update {
                ProfileDetailUiState(
                    profile = Profile(
                        id = 0,
                        name = "",
                        colorHex = Profile.PRESET_COLORS[0],
                        minSpeed = 1.39f,
                        maxSpeed = 16.67f,
                        minVolumePercent = 20,
                        maxVolumePercent = 50,
                        speedUnit = SpeedUnit.KILOMETERS_PER_HOUR,
                    ),
                    isNewProfile = true
                )
            }
        }
    }

    private fun loadProfile(id: Long) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val profile = getProfileByIdUseCase(id)
            if (profile != null) {
                _uiState.update {
                    ProfileDetailUiState(
                        profile = profile,
                        isLoading = false,
                        isNewProfile = false
                    )
                }
            } else {
                // Profile not found, handle error
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nameError = "Profile not found"
                    )
                }
            }
        }
    }

    fun updateName(name: String) {
        val currentProfile = _uiState.value.profile ?: return
        _uiState.update {
            it.copy(
                profile = currentProfile.copy(name = name),
                nameError = null
            )
        }
    }

    fun updateColor(colorHex: String) {
        val currentProfile = _uiState.value.profile ?: return
        _uiState.update {
            it.copy(
                profile = currentProfile.copy(colorHex = colorHex)
            )
        }
    }

    fun updateMinSpeed(speed: Float) {
        val currentProfile = _uiState.value.profile ?: return
        _uiState.update {
            it.copy(
                profile = currentProfile.copy(minSpeed = speed)
            )
        }
    }

    fun updateMaxSpeed(speed: Float) {
        val currentProfile = _uiState.value.profile ?: return
        _uiState.update {
            it.copy(
                profile = currentProfile.copy(maxSpeed = speed)
            )
        }
    }

    fun updateMinVolume(volume: Int) {
        val currentProfile = _uiState.value.profile ?: return
        _uiState.update {
            it.copy(
                profile = currentProfile.copy(minVolumePercent = volume)
            )
        }
    }

    fun updateMaxVolume(volume: Int) {
        val currentProfile = _uiState.value.profile ?: return
        _uiState.update {
            it.copy(
                profile = currentProfile.copy(maxVolumePercent = volume)
            )
        }
    }

    fun updateSpeedUnit(unit: SpeedUnit) {
        val currentProfile = _uiState.value.profile ?: return
        _uiState.update {
            it.copy(profile = currentProfile.copy(speedUnit = unit))
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val profile = _uiState.value.profile ?: return@launch

            // Validate name
            val validation = validateProfileNameUseCase(profile.name, profile.id)
            if (!validation.isValid) {
                _uiState.update { it.copy(nameError = validation.errorMessage) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            try {
                saveProfileUseCase(profile)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nameError = "Failed to save profile"
                    )
                }
            }
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            val profile = _uiState.value.profile ?: return@launch
            if (profile.id == 0L) return@launch // Can't delete unsaved profile

            _uiState.update { it.copy(isLoading = true) }

            try {
                deleteProfileUseCase(profile)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDeleted = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}
