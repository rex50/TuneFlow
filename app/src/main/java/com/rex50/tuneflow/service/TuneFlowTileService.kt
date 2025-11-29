package com.rex50.tuneflow.service

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import com.rex50.tuneflow.ui.ServiceTrampolineActivity
import com.rex50.tuneflow.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TuneFlowTileService : TileService() {

    @Inject
    lateinit var volumeSettingsRepository: VolumeSettingsRepository

    private var serviceScope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Main + Job())
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope?.cancel()
    }

    override fun onStartListening() {
        super.onStartListening()
        // Always update tile state when user views Quick Settings
        // This ensures tile reflects the actual state from repository
        updateTileState()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTileState()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        // Optional: Stop service when tile is removed
    }

    override fun onClick() {
        super.onClick()

        serviceScope?.launch {
            val currentSettings = volumeSettingsRepository.settings.first()
            val newState = !currentSettings.isServiceEnabled

            // If trying to enable the service, check for required permissions
            if (newState) {
                if (!hasRequiredPermissions()) {
                    // Show toast and keep service disabled
                    showToast(Constants.Messages.PERMISSION_REQUIRED)
                    updateTileState()
                    return@launch
                }

                // Update the settings
                volumeSettingsRepository.updateServiceEnabled(newState)

                // Use broadcast to start service - works smoothly without activity launch
                sendServiceBroadcast(true)
            } else {
                // Update the settings
                volumeSettingsRepository.updateServiceEnabled(newState)

                // Use broadcast to stop service
                sendServiceBroadcast(false)
            }

            // Update the tile to reflect new state
            updateTileState()
        }
    }

    private fun sendServiceBroadcast(start: Boolean) {
        try {
            // For Android 14+ (API 34+), use trampoline activity to ensure app is in foreground
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startTrampolineActivity(start)
            } else {
                // For older versions, use explicit broadcast with ComponentName
                val action = if (start) {
                    Constants.Actions.ACTION_START_SERVICE
                } else {
                    Constants.Actions.ACTION_STOP_SERVICE
                }

                val intent = Intent(action).apply {
                    component = ComponentName(
                        applicationContext,
                        Constants.ComponentNames.SERVICE_CONTROL_RECEIVER
                    )
                }

                try {
                    applicationContext.sendBroadcast(intent)
                    Log.d(Constants.LogTags.TILE_SERVICE, "Broadcast sent: $action")
                } catch (e: Exception) {
                    Log.e(Constants.LogTags.TILE_SERVICE, "Broadcast failed, using fallback", e)
                    // Fallback to direct service call
                    fallbackServiceCall(start)
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.LogTags.TILE_SERVICE, "Service broadcast failed", e)
            // Last resort fallback
            fallbackServiceCall(start)
        }
    }

    private fun fallbackServiceCall(start: Boolean) {
        if (start) {
            VolumeControlService.startService(applicationContext)
        } else {
            VolumeControlService.stopService(applicationContext)
        }
    }

    private fun startTrampolineActivity(start: Boolean) {
        val action = if (start) {
            Constants.Actions.ACTION_START_SERVICE_TRAMPOLINE
        } else {
            Constants.Actions.ACTION_STOP_SERVICE_TRAMPOLINE
        }

        val requestCode = if (start) {
            Constants.RequestCodes.PENDING_INTENT_START_SERVICE
        } else {
            Constants.RequestCodes.PENDING_INTENT_STOP_SERVICE
        }

        val intent = Intent(applicationContext, ServiceTrampolineActivity::class.java).apply {
            this.action = action
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use startActivityAndCollapse which is specifically designed for Quick Settings tiles
        // This bypasses background activity launch restrictions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                startActivityAndCollapse(pendingIntent)
                Log.d(Constants.LogTags.TILE_SERVICE, "Trampoline activity launched: $action")
            } catch (e: Exception) {
                Log.e(Constants.LogTags.TILE_SERVICE, "Failed to launch trampoline activity", e)
                // If this fails, fall back to direct service start (may still fail with SecurityException)
                fallbackServiceCall(start)
            }
        }
    }

    /**
     * Checks if all required permissions are granted to start the location service
     *
     * @return true if all required permissions are granted, false otherwise
     */
    private fun hasRequiredPermissions(): Boolean {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Check for location permissions - at least one must be granted
        val hasLocationPermission = hasFineLocation || hasCoarseLocation

        // For Android 14+ (API 34+), also check FOREGROUND_SERVICE_LOCATION
        val hasForegroundServicePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required for older versions
        }

        return hasLocationPermission && hasForegroundServicePermission
    }

    private fun showToast(message: String) {
        serviceScope?.launch(Dispatchers.Main) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateTileState() {
        serviceScope?.launch {
            val settings = volumeSettingsRepository.settings.first()
            val tile = qsTile ?: return@launch

            if (settings.isServiceEnabled) {
                tile.state = Tile.STATE_ACTIVE
                tile.label = getString(R.string.tile_label_active)
                tile.contentDescription = getString(R.string.tile_description_active)
            } else {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.tile_label_inactive)
                tile.contentDescription = getString(R.string.tile_description_inactive)
            }

            // Update the tile
            tile.updateTile()
        }
    }
}

