package com.rex50.tuneflow.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.IBinder
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
import kotlin.math.sqrt

@AndroidEntryPoint
class VolumeControlService : Service(), SensorEventListener {

    @Inject
    lateinit var volumeSettingsRepository: VolumeSettingsRepository

    @Inject
    lateinit var serviceStateRepository: ServiceStateRepository

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var audioManager: AudioManager
    private lateinit var preferencesManager: PreferencesManager

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var currentSettings: com.rex50.tuneflow.domain.model.VolumeSettings? = null

    private var lastAcceleration = 0f
    private var currentAcceleration = 0f

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

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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

        accelerometer?.also { acc ->
            sensorManager.registerListener(
                this,
                acc,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.launch {
            volumeSettingsRepository.updateServiceEnabled(false)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate total acceleration
            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Subtract gravity (9.8 m/s²) to get device acceleration
            val deviceAcceleration = Math.abs(acceleration - SensorManager.GRAVITY_EARTH)

            // Apply smoothing to reduce jitter
            currentAcceleration = (SMOOTHING_FACTOR * lastAcceleration) +
                                 ((1 - SMOOTHING_FACTOR) * deviceAcceleration)
            lastAcceleration = currentAcceleration

            // Update volume based on acceleration
            serviceScope.launch {
                delay(UPDATE_DELAY_MS)
                updateVolume(currentAcceleration)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }

    private fun updateVolume(acceleration: Float) {
        val settings = currentSettings ?: return

        // Get device's maximum volume level
        val maxDeviceVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // Map acceleration to volume percentage
        val normalizedAcceleration = when {
            acceleration <= settings.minAcceleration -> 0f
            acceleration >= settings.maxAcceleration -> 1f
            else -> (acceleration - settings.minAcceleration) /
                   (settings.maxAcceleration - settings.minAcceleration)
        }

        // Calculate target volume as percentage of device max
        val volumePercentRange = settings.maxVolumePercent - settings.minVolumePercent
        val targetVolumePercent = settings.minVolumePercent + (volumePercentRange * normalizedAcceleration).toInt()

        // Convert percentage to actual device volume level
        val targetVolume = (maxDeviceVolume * targetVolumePercent / 100f).toInt()
            .coerceIn(0, maxDeviceVolume)

        // Set the media volume
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            targetVolume,
            0
        )

        // Update notification with current acceleration and volume percentage
        updateNotification(acceleration, targetVolumePercent)

        serviceScope.launch {
            serviceStateRepository.updateState(ServiceState(acceleration, targetVolumePercent))
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Volume Control Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls media volume based on driving acceleration"
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
            .setContentText("Monitoring acceleration for volume control")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(acceleration: Float, volumePercent: Int) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val settings = currentSettings
        val unit = settings?.accelerationUnit ?: com.rex50.tuneflow.domain.model.AccelerationUnit.METERS_PER_SECOND_SQUARED
        val displayValue = unit.convertFromMps2(acceleration)

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
