package com.rex50.tuneflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rex50.tuneflow.domain.model.SpeedUnit
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "volume_settings")

class PreferencesManager(private val context: Context) : VolumeSettingsRepository {

    companion object {
        private val MIN_VOLUME_PERCENT = intPreferencesKey("min_volume_percent")
        private val MAX_VOLUME_PERCENT = intPreferencesKey("max_volume_percent")
        private val MIN_SPEED = floatPreferencesKey("min_speed")
        private val MAX_SPEED = floatPreferencesKey("max_speed")
        private val IS_SERVICE_ENABLED = booleanPreferencesKey("is_service_enabled")
        private val SPEED_UNIT = intPreferencesKey("speed_unit")
        private val ACCELERATION_UNIT = intPreferencesKey("acceleration_unit")
    }

    override val settings: Flow<VolumeSettings> = context.dataStore.data.map { preferences ->
        VolumeSettings(
            minVolumePercent = preferences[MIN_VOLUME_PERCENT] ?: 20,
            maxVolumePercent = preferences[MAX_VOLUME_PERCENT] ?: 60,
            minSpeed = preferences[MIN_SPEED] ?: 1.39f, // Default: 5 km/h
            maxSpeed = preferences[MAX_SPEED] ?: 27.78f, // Default: 100 km/h
            isServiceEnabled = preferences[IS_SERVICE_ENABLED] ?: false,
            speedUnit = SpeedUnit.fromOrdinal(preferences[SPEED_UNIT] ?: preferences[ACCELERATION_UNIT] ?: 1) // Migrate from old key
        )
    }

    override suspend fun updateMinVolume(volume: Int) {
        context.dataStore.edit { preferences ->
            preferences[MIN_VOLUME_PERCENT] = volume
        }
    }

    override suspend fun updateMaxVolume(volume: Int) {
        context.dataStore.edit { preferences ->
            preferences[MAX_VOLUME_PERCENT] = volume
        }
    }

    override suspend fun updateMinSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[MIN_SPEED] = speed
        }
    }

    override suspend fun updateMaxSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[MAX_SPEED] = speed
        }
    }

    override suspend fun updateServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SERVICE_ENABLED] = enabled
        }
    }

    override suspend fun updateSpeedUnit(unit: SpeedUnit) {
        context.dataStore.edit { preferences ->
            preferences[SPEED_UNIT] = unit.ordinal
        }
    }
}
