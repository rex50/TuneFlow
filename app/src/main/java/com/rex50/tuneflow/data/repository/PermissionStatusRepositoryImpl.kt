package com.rex50.tuneflow.data.repository

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.PermissionStatus
import com.rex50.tuneflow.domain.model.PermissionType
import com.rex50.tuneflow.domain.repository.PermissionStatusRepository
import com.rex50.tuneflow.ui.PermissionEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionStatusRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionStatusRepository {

    private val _permissionStatus = MutableStateFlow<List<PermissionStatus>>(emptyList())
    private val _permissionEvents = MutableSharedFlow<PermissionEvent>()

    override fun observePermissionStatus(): Flow<List<PermissionStatus>> {
        return _permissionStatus.asStateFlow()
    }

    override fun observePermissionEvents(): Flow<PermissionEvent> {
        return _permissionEvents
    }

    override suspend fun handleEvent(event: PermissionEvent) {
        _permissionEvents.emit(event)
    }

    override fun checkPermissions(activity: Activity) {
        updatePermissionStatus(activity)
    }

    override fun checkGpsEnabled(activity: Activity) {
        // Re-evaluate with last known activity
        updatePermissionStatus(activity)
    }

    override fun refreshStatus(activity: Activity) {
        updatePermissionStatus(activity)
    }

    private fun updatePermissionStatus(activity: Activity) {
        val statuses = mutableListOf<PermissionStatus>()

        // Check Fine Location
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val canRequestFine = !fineLocationGranted &&
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).not()

        statuses.add(
            PermissionStatus(
                type = PermissionType.FINE_LOCATION,
                isGranted = fineLocationGranted,
                canRequest = canRequestFine,
                titleRes = R.string.permission_fine_location_title,
                descriptionRes = R.string.permission_fine_location_description
            )
        )

        // Check Coarse Location
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val canRequestCoarse = !coarseLocationGranted &&
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).not()

        statuses.add(
            PermissionStatus(
                type = PermissionType.COARSE_LOCATION,
                isGranted = coarseLocationGranted,
                canRequest = canRequestCoarse,
                titleRes = R.string.permission_coarse_location_title,
                descriptionRes = R.string.permission_coarse_location_description
            )
        )

        // Check GPS Enabled
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        statuses.add(
            PermissionStatus(
                type = PermissionType.GPS_ENABLED,
                isGranted = gpsEnabled,
                canRequest = true, // GPS can always be requested via settings dialog
                titleRes = R.string.permission_gps_title,
                descriptionRes = R.string.permission_gps_description
            )
        )

        // Check POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            val canRequestNotification = !notificationGranted &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ).not()

            statuses.add(
                PermissionStatus(
                    type = PermissionType.POST_NOTIFICATIONS,
                    isGranted = notificationGranted,
                    canRequest = canRequestNotification,
                    titleRes = R.string.permission_notification_title,
                    descriptionRes = R.string.permission_notification_description
                )
            )
        }

        _permissionStatus.value = statuses
    }
}

