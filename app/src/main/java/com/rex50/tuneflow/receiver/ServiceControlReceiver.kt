package com.rex50.tuneflow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rex50.tuneflow.service.VolumeControlService
import com.rex50.tuneflow.utils.Constants

/**
 * BroadcastReceiver to handle service start/stop from Quick Settings tile
 *
 * Used on Android 13 and below to start/stop the VolumeControlService without
 * requiring an activity launch. On Android 14+, the trampoline activity is used instead.
 */
class ServiceControlReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (val action = intent.action) {
            Constants.Actions.ACTION_START_SERVICE -> handleStartService(context)
            Constants.Actions.ACTION_STOP_SERVICE -> handleStopService(context)
            else -> Log.w(TAG, "Unknown action received: $action")
        }
    }

    private fun handleStartService(context: Context) {
        try {
            VolumeControlService.startService(context)
            Log.d(TAG, "Service start requested successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception when starting service - missing permissions?", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service", e)
        }
    }

    private fun handleStopService(context: Context) {
        try {
            VolumeControlService.stopService(context)
            Log.d(TAG, "Service stop requested successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop service", e)
        }
    }

    companion object {
        private const val TAG = Constants.LogTags.SERVICE_CONTROL_RECEIVER
    }
}

