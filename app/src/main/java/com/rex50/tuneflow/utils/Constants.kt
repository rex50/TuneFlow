package com.rex50.tuneflow.utils

/**
 * Application-wide constants
 */
object Constants {

    /**
     * Broadcast actions for service control
     */
    object Actions {
        const val ACTION_START_SERVICE = "com.rex50.tuneflow.ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "com.rex50.tuneflow.ACTION_STOP_SERVICE"
        const val ACTION_START_SERVICE_TRAMPOLINE = "com.rex50.tuneflow.START_SERVICE_TRAMPOLINE"
        const val ACTION_STOP_SERVICE_TRAMPOLINE = "com.rex50.tuneflow.STOP_SERVICE_TRAMPOLINE"
    }

    /**
     * PendingIntent request codes
     */
    object RequestCodes {
        const val PENDING_INTENT_START_SERVICE = 1001
        const val PENDING_INTENT_STOP_SERVICE = 1002
    }

    /**
     * Fully qualified class names for ComponentName
     */
    object ComponentNames {
        const val SERVICE_CONTROL_RECEIVER = "com.rex50.tuneflow.receiver.ServiceControlReceiver"
    }

    /**
     * Logging tags
     */
    object LogTags {
        const val TILE_SERVICE = "TuneFlowTileService"
        const val SERVICE_TRAMPOLINE = "ServiceTrampoline"
        const val SERVICE_CONTROL_RECEIVER = "ServiceControlReceiver"
        @Suppress("unused") // Used in VolumeControlService
        const val VOLUME_CONTROL_SERVICE = "VolumeControlService"
    }

    /**
     * Timing constants
     */
    object Delays {
        const val TRAMPOLINE_ACTIVITY_DELAY_MS = 100L
    }

    /**
     * Toast messages
     */
    object Messages {
        const val PERMISSION_REQUIRED = "Please grant location permissions in the app first"
    }
}

