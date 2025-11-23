package com.rex50.tuneflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.domain.repository.VolumeSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "volume_settings")

class PreferencesManager(private val context: Context) : VolumeSettingsRepository {

    companion object {
        private val MIN_VOLUME = intPreferencesKey("min_volume")
        private val MAX_VOLUME = intPreferencesKey("max_volume")
        private val MIN_ACCELERATION = floatPreferencesKey("min_acceleration")
        private val MAX_ACCELERATION = floatPreferencesKey("max_acceleration")
        private val IS_SERVICE_ENABLED = booleanPreferencesKey("is_service_enabled")
    }

    override val settings: Flow<VolumeSettings> = context.dataStore.data.map { preferences ->
        VolumeSettings(
            minVolume = preferences[MIN_VOLUME] ?: 5,
            maxVolume = preferences[MAX_VOLUME] ?: 15,
            minAcceleration = preferences[MIN_ACCELERATION] ?: 0f,
            maxAcceleration = preferences[MAX_ACCELERATION] ?: 10f,
            isServiceEnabled = preferences[IS_SERVICE_ENABLED] ?: false
        )
    }

    override suspend fun updateMinVolume(volume: Int) {
        context.dataStore.edit { preferences ->
            preferences[MIN_VOLUME] = volume
        }
    }

    override suspend fun updateMaxVolume(volume: Int) {
        context.dataStore.edit { preferences ->
            preferences[MAX_VOLUME] = volume
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
}
