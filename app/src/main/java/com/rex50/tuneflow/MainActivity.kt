package com.rex50.tuneflow

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.rex50.tuneflow.domain.repository.PermissionStatusRepository
import com.rex50.tuneflow.ui.PermissionEvent
import com.rex50.tuneflow.ui.screens.HomeScreen
import com.rex50.tuneflow.ui.theme.TuneFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionStatusRepository: PermissionStatusRepository


    private val locationPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        Log.d("MainActivity", "Location permissions - Fine: $fineLocationGranted, Coarse: $coarseLocationGranted")

        // Refresh permission status after result
        permissionStatusRepository.refreshStatus(this@MainActivity)
    }

    private val gpsResolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("MainActivity", "GPS enabled successfully")
        } else {
            Log.d("MainActivity", "GPS enable request denied")
        }
        // Refresh GPS status after result
        permissionStatusRepository.checkGpsEnabled(this)
    }

    private val batteryOptimizationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Refresh battery optimization status after result
        permissionStatusRepository.refreshStatus(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuneFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        viewModel = hiltViewModel()
                    )
                }
            }
        }

        lifecycleScope.launch {
            permissionStatusRepository.observePermissionEvents().collect {
                handlePermissionEvent(it)
            }
        }

        // Check permissions on startup
        permissionStatusRepository.checkPermissions(this@MainActivity)
        permissionStatusRepository.checkGpsEnabled(this@MainActivity)
    }

    override fun onResume() {
        super.onResume()
        // Refresh permission status when returning to app
        permissionStatusRepository.refreshStatus(this@MainActivity)
    }


    private fun handlePermissionEvent(event: PermissionEvent) {
        when (event) {
            is PermissionEvent.RequestPermissions -> {
                locationPermissionsLauncher.launch(event.permissions.toTypedArray())
            }
            is PermissionEvent.RequestGpsEnable -> {
                requestGpsEnable()
            }
            is PermissionEvent.OpenSettings -> {
                openAppSettings()
            }
            is PermissionEvent.GpsResolutionFailed -> {
                Toast.makeText(
                    this,
                    R.string.permission_gps_resolution_failed,
                    Toast.LENGTH_LONG
                ).show()
                openAppSettings()
            }
            is PermissionEvent.RequestBatteryOptimization -> {
                requestBatteryOptimization()
            }
        }
    }

    private fun requestGpsEnable() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L
            ).build()

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)

            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                // GPS is already enabled
                Log.d("MainActivity", "GPS already enabled")
                permissionStatusRepository.checkGpsEnabled(this)
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        // Show the dialog to enable GPS
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        gpsResolutionLauncher.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.e("MainActivity", "Error launching GPS resolution", sendEx)
                        handlePermissionEvent(PermissionEvent.GpsResolutionFailed)
                    }
                } else {
                    // Google Play Services not available, fallback to settings
                    Log.e("MainActivity", "GPS resolution not available", exception)
                    handlePermissionEvent(PermissionEvent.GpsResolutionFailed)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error requesting GPS enable", e)
            handlePermissionEvent(PermissionEvent.GpsResolutionFailed)
        }
    }

    private fun requestBatteryOptimization() {
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = "package:$packageName".toUri()
        batteryOptimizationLauncher.launch(intent)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}