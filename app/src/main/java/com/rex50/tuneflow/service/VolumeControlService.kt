package com.rex50.tuneflow.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.rex50.tuneflow.MainActivity
import com.rex50.tuneflow.R
import com.rex50.tuneflow.data.PreferencesManager
import com.rex50.tuneflow.domain.model.ServiceState
import com.rex50.tuneflow.domain.repository.ServiceStateRepository
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VolumeControlService : Service(), LocationListener {

    @Inject
    lateinit var volumeSettingsRepository: VolumeSettingsRepository

    @Inject
    lateinit var serviceStateRepository: ServiceStateRepository

    private lateinit var locationManager: LocationManager
    private lateinit var audioManager: AudioManager
    private lateinit var preferencesManager: PreferencesManager

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var currentSettings: com.rex50.tuneflow.domain.model.VolumeSettings? = null

    private var lastSpeed = 0f

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "VolumeControlChannel"
        private const val SMOOTHING_FACTOR = 0.8f
        private const val UPDATE_DELAY_MS = 500L

        fun startService(context: Context) {
            val intent = Intent(context, VolumeControlService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, VolumeControlService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        preferencesManager = PreferencesManager(this)

        serviceScope.launch {
            volumeSettingsRepository.settings.collect { settings ->
                currentSettings = settings
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Request location updates from GPS provider
            // Using 2-second intervals with 5-meter threshold for battery efficiency
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L, // Update every 2 seconds
                0.5f,    // Minimum distance of 5 meters between updates
                this
            )
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
        serviceScope.launch {
            volumeSettingsRepository.updateServiceEnabled(false)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onLocationChanged(location: Location) {
        // Get speed from GPS in m/s
        val gpsSpeed = location.speed

        // Apply smoothing to reduce jitter
        val currentSpeed = (SMOOTHING_FACTOR * lastSpeed) +
                      ((1 - SMOOTHING_FACTOR) * gpsSpeed)
        lastSpeed = currentSpeed

        // Update volume based on speed
        serviceScope.launch {
            delay(UPDATE_DELAY_MS)
            updateVolume(currentSpeed)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {
        // Not needed for this implementation
    }

    override fun onProviderEnabled(provider: String) {
        // Not needed for this implementation
    }

    override fun onProviderDisabled(provider: String) {
        // Not needed for this implementation
    }

    private fun updateVolume(speed: Float) {
        val settings = currentSettings ?: return

        // Get device's maximum volume level
        val maxDeviceVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // Map speed to volume percentage
        val normalizedSpeed = when {
            speed <= settings.minAcceleration -> 0f
            speed >= settings.maxAcceleration -> 1f
            else -> (speed - settings.minAcceleration) /
                   (settings.maxAcceleration - settings.minAcceleration)
        }

        // Calculate target volume as percentage of device max
        val volumePercentRange = settings.maxVolumePercent - settings.minVolumePercent
        val targetVolumePercent = settings.minVolumePercent + (volumePercentRange * normalizedSpeed).toInt()

        // Convert percentage to actual device volume level
        val targetVolume = (maxDeviceVolume * targetVolumePercent / 100f).toInt()
            .coerceIn(0, maxDeviceVolume)

        // Set the media volume
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            targetVolume,
            0
        )

        // Update notification with current speed and volume percentage
        updateNotification(speed, targetVolumePercent)

        serviceScope.launch {
            serviceStateRepository.updateState(ServiceState(speed, targetVolumePercent))
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Volume Control Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls media volume based on driving speed"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TuneFlow Active")
            .setContentText("Monitoring speed for volume control")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(speed: Float, volumePercent: Int) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val settings = currentSettings
        val unit = settings?.accelerationUnit ?: com.rex50.tuneflow.domain.model.AccelerationUnit.METERS_PER_SECOND_SQUARED
        val displayValue = unit.convertFromMps2(speed)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TuneFlow Active")
            .setContentText("%.1f %s | Volume: %d%%".format(displayValue, unit.getLabel(), volumePercent))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
