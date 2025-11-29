package com.rex50.tuneflow.domain.model

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