package com.rex50.tuneflow.domain.model

/**
 * Represents the unit of measurement for acceleration/speed display.
 */
enum class AccelerationUnit {
    /** Meters per second squared - raw acceleration */
    METERS_PER_SECOND_SQUARED,

    /** Kilometers per hour - speed */
    KILOMETERS_PER_HOUR,

    /** Miles per hour - speed */
    MILES_PER_HOUR;

    /**
     * Get the display label for the unit
     */
    fun getLabel(): String = when (this) {
        METERS_PER_SECOND_SQUARED -> "m/s²"
        KILOMETERS_PER_HOUR -> "km/h"
        MILES_PER_HOUR -> "mph"
    }

    /**
     * Get the full name of the unit
     */
    fun getDisplayName(): String = when (this) {
        METERS_PER_SECOND_SQUARED -> "Acceleration (m/s²)"
        KILOMETERS_PER_HOUR -> "Speed (km/h)"
        MILES_PER_HOUR -> "Speed (mph)"
    }

    /**
     * Convert from m/s (speed) to this unit's representation
     * Note: Method name retained as convertFromMps2 for API compatibility,
     * but now converts speed values (m/s) rather than acceleration (m/s²)
     */
    fun convertFromMps2(valueInMps2: Float): Float = when (this) {
        METERS_PER_SECOND_SQUARED -> valueInMps2
        KILOMETERS_PER_HOUR -> valueInMps2 * 3.6f // m/s to km/h
        MILES_PER_HOUR -> valueInMps2 * 2.23694f // m/s to mph
    }

    /**
     * Convert from this unit to m/s (speed)
     * Note: Method name retained as convertToMps2 for API compatibility,
     * but now converts to speed values (m/s) rather than acceleration (m/s²)
     */
    fun convertToMps2(value: Float): Float = when (this) {
        METERS_PER_SECOND_SQUARED -> value
        KILOMETERS_PER_HOUR -> value / 3.6f
        MILES_PER_HOUR -> value / 2.23694f
    }

    companion object {
        fun fromOrdinal(ordinal: Int): AccelerationUnit =
            entries.getOrNull(ordinal) ?: KILOMETERS_PER_HOUR

        fun getMinRange(unit: AccelerationUnit): Float = when (unit) {
            METERS_PER_SECOND_SQUARED -> 0f
            KILOMETERS_PER_HOUR -> 5f
            MILES_PER_HOUR -> 3f
        }

        fun getMaxRangeForMin(unit: AccelerationUnit): Float = when (unit) {
            METERS_PER_SECOND_SQUARED -> 5f
            KILOMETERS_PER_HOUR -> 100f // 5-100 km/h range
            MILES_PER_HOUR -> 62f // ~5-62 mph range
        }

        fun getMaxRangeForMax(unit: AccelerationUnit): Float = when (unit) {
            METERS_PER_SECOND_SQUARED -> 20f
            KILOMETERS_PER_HOUR -> 100f // Max 100 km/h
            MILES_PER_HOUR -> 62f // Max 62 mph
        }

        fun getMinRangeForMax(unit: AccelerationUnit): Float = when (unit) {
            METERS_PER_SECOND_SQUARED -> 5f
            KILOMETERS_PER_HOUR -> 5f
            MILES_PER_HOUR -> 3f
        }
    }
}

data class VolumeSettings(
    val isServiceEnabled: Boolean = false,
    val maxAcceleration: Float = 27.78f, // Default: 100 km/h in m/s²
    val minAcceleration: Float = 1.39f, // Default: 5 km/h in m/s²
    val maxVolumePercent: Int = 60, // Default: 60% of device max volume
    val minVolumePercent: Int = 20, // Default: 20% of device max volume
    val accelerationUnit: AccelerationUnit = AccelerationUnit.KILOMETERS_PER_HOUR
)
