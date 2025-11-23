package com.rex50.tuneflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rex50.tuneflow.domain.model.AccelerationUnit
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "volume_settings")

class PreferencesManager(private val context: Context) : VolumeSettingsRepository {

    companion object {
        private val MIN_VOLUME_PERCENT = intPreferencesKey("min_volume_percent")
        private val MAX_VOLUME_PERCENT = intPreferencesKey("max_volume_percent")
        private val MIN_ACCELERATION = floatPreferencesKey("min_acceleration")
        private val MAX_ACCELERATION = floatPreferencesKey("max_acceleration")
        private val IS_SERVICE_ENABLED = booleanPreferencesKey("is_service_enabled")
        private val ACCELERATION_UNIT = intPreferencesKey("acceleration_unit")
    }

    override val settings: Flow<VolumeSettings> = context.dataStore.data.map { preferences ->
        VolumeSettings(
            minVolumePercent = preferences[MIN_VOLUME_PERCENT] ?: 20,
            maxVolumePercent = preferences[MAX_VOLUME_PERCENT] ?: 60,
            minAcceleration = preferences[MIN_ACCELERATION] ?: 1.39f, // Default: 5 km/h
            maxAcceleration = preferences[MAX_ACCELERATION] ?: 27.78f, // Default: 100 km/h
            isServiceEnabled = preferences[IS_SERVICE_ENABLED] ?: false,
            accelerationUnit = AccelerationUnit.fromOrdinal(preferences[ACCELERATION_UNIT] ?: 1) // 1 = KILOMETERS_PER_HOUR
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

    override suspend fun updateMinAcceleration(acceleration: Float) {
        context.dataStore.edit { preferences ->
            preferences[MIN_ACCELERATION] = acceleration
        }
    }

    override suspend fun updateMaxAcceleration(acceleration: Float) {
        context.dataStore.edit { preferences ->
            preferences[MAX_ACCELERATION] = acceleration
        }
    }

    override suspend fun updateServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SERVICE_ENABLED] = enabled
        }
    }

    override suspend fun updateAccelerationUnit(unit: AccelerationUnit) {
        context.dataStore.edit { preferences ->
            preferences[ACCELERATION_UNIT] = unit.ordinal
        }
    }
}
