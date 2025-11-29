package com.rex50.tuneflow.domain.repository

import android.app.Activity
import com.rex50.tuneflow.domain.model.PermissionEvent
import com.rex50.tuneflow.domain.model.PermissionStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing permission status checks
 */
interface PermissionStatusRepository {
    /**
     * Observe the current status of all required permissions
     */
    fun observePermissionStatus(): Flow<List<PermissionStatus>>

    /**
     * Observe permission-related events
     */
    fun observePermissionEvents(): Flow<PermissionEvent>

    /**
     * Handle permission-related events
     */
    suspend fun handleEvent(event: PermissionEvent)

    /**
     * Check and update permission status based on current activity context
     * @param activity Activity context needed for shouldShowRequestPermissionRationale
     */
    fun checkPermissions(activity: Activity)

    /**
     * Check if GPS is enabled
     * @param activity Application context
     */
    fun checkGpsEnabled(activity: Activity)

    /**
     * Trigger a refresh of permission status
     * @param activity Activity context
     */
    fun refreshStatus(activity: Activity)

    /**
     * Check if battery optimization is disabled for the app
     * @return true if battery optimization is disabled (exempted)
     */
    fun isBatteryOptimizationDisabled(): Boolean
}
