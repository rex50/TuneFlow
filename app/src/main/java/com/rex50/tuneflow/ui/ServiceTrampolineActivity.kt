package com.rex50.tuneflow.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rex50.tuneflow.service.VolumeControlService
import com.rex50.tuneflow.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Transparent trampoline activity to start the foreground service on Android 14+
 *
 * This activity ensures the app is in the foreground when starting the location service,
 * which is required on Android 14+ to start a foreground service with FOREGROUND_SERVICE_TYPE_LOCATION.
 *
 * This activity is launched via PendingIntent from the Quick Settings tile using
 * startActivityAndCollapse(), which bypasses background activity launch restrictions.
 *
 * The activity uses a transparent theme and closes immediately after starting the service,
 * minimizing disruption to the user experience.
 */
@AndroidEntryPoint
class ServiceTrampolineActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action
        Log.d(TAG, "Activity created with action: $action")

        lifecycleScope.launch {
            // Small delay to ensure activity is fully in foreground
            // This is critical for location foreground service on Android 14+
            delay(Constants.Delays.TRAMPOLINE_ACTIVITY_DELAY_MS)

            try {
                when (action) {
                    Constants.Actions.ACTION_START_SERVICE_TRAMPOLINE -> handleStartService()
                    Constants.Actions.ACTION_STOP_SERVICE_TRAMPOLINE -> handleStopService()
                    else -> Log.w(TAG, "Unknown action: $action")
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception - missing permissions?", e)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start/stop service", e)
            } finally {
                // Always close the activity
                finish()
            }
        }
    }

    /**
     * Handles starting the VolumeControlService
     */
    private fun handleStartService() {
        Log.d(TAG, "Starting VolumeControlService")
        VolumeControlService.startService(applicationContext)
    }

    /**
     * Handles stopping the VolumeControlService
     */
    private fun handleStopService() {
        Log.d(TAG, "Stopping VolumeControlService")
        VolumeControlService.stopService(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity destroyed")
    }

    companion object {
        private const val TAG = Constants.LogTags.SERVICE_TRAMPOLINE
    }
}

