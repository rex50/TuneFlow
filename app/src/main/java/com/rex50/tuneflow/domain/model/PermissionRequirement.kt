package com.rex50.tuneflow.domain.model

import androidx.annotation.StringRes

/**
 * Types of permissions required by the app
 */
enum class PermissionType {
    FINE_LOCATION,
    COARSE_LOCATION,
    GPS_ENABLED,
    POST_NOTIFICATIONS,
    BATTERY_OPTIMIZATION
}

/**
 * Represents the status of a single permission requirement
 */
data class PermissionStatus(
    val type: PermissionType,
    val isGranted: Boolean,
    val canRequest: Boolean,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
)

/**
 * UI state for permissions
 */
sealed interface PermissionsUiState {
    /**
     * All permissions are granted and GPS is enabled
     */
    data object AllGranted : PermissionsUiState

    /**
     * Some permissions are missing or GPS is disabled
     */
    data class MissingRequirements(
        val requirements: List<PermissionStatus>
    ) : PermissionsUiState
}
