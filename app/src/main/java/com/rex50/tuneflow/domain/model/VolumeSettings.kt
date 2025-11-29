package com.rex50.tuneflow.domain.model

/**
 * Represents the unit of measurement for speed display.
 */
enum class SpeedUnit {
    /** Kilometers per hour - speed */
    KILOMETERS_PER_HOUR,

    /** Miles per hour - speed */
    MILES_PER_HOUR;

    /**
     * Get the display label for the unit
     */
    fun getLabel(): String = when (this) {
        KILOMETERS_PER_HOUR -> "km/h"
        MILES_PER_HOUR -> "mph"
    }

    /**
     * Get the full name of the unit
     */
    fun getDisplayName(): String = when (this) {
        KILOMETERS_PER_HOUR -> "Speed (km/h)"
        MILES_PER_HOUR -> "Speed (mph)"
    }

    /**
     * Convert from m/s (speed) to this unit's representation
     */
    fun convertFromMps(valueInMps: Float): Float = when (this) {
        KILOMETERS_PER_HOUR -> valueInMps * 3.6f // m/s to km/h
        MILES_PER_HOUR -> valueInMps * 2.23694f // m/s to mph
    }

    /**
     * Convert from this unit to m/s (speed)
     */
    fun convertToMps(value: Float): Float = when (this) {
        KILOMETERS_PER_HOUR -> value / 3.6f
        MILES_PER_HOUR -> value / 2.23694f
    }

    companion object {
        /**
         * Get SpeedUnit from ordinal value with migration support.
         * Legacy ordinal 0 (m/s²) is migrated to KILOMETERS_PER_HOUR.
         */
        fun fromOrdinal(ordinal: Int): SpeedUnit = when (ordinal) {
            0 -> KILOMETERS_PER_HOUR // Migrate legacy m/s² to km/h
            1 -> KILOMETERS_PER_HOUR
            2 -> MILES_PER_HOUR
            else -> KILOMETERS_PER_HOUR
        }

        fun getMinRange(unit: SpeedUnit): Float = when (unit) {
            KILOMETERS_PER_HOUR -> 5f
            MILES_PER_HOUR -> 3f
        }

        fun getMaxRangeForMin(unit: SpeedUnit): Float = when (unit) {
            KILOMETERS_PER_HOUR -> 100f // 5-100 km/h range
            MILES_PER_HOUR -> 62f // ~5-62 mph range
        }

        fun getMaxRangeForMax(unit: SpeedUnit): Float = when (unit) {
            KILOMETERS_PER_HOUR -> 100f // Max 100 km/h
            MILES_PER_HOUR -> 62f // Max 62 mph
        }

        fun getMinRangeForMax(unit: SpeedUnit): Float = when (unit) {
            KILOMETERS_PER_HOUR -> 5f
            MILES_PER_HOUR -> 3f
        }
    }
}

data class VolumeSettings(
    val isServiceEnabled: Boolean = false,
    val maxSpeed: Float = 27.78f, // Default: 100 km/h in m/s
    val minSpeed: Float = 1.39f, // Default: 5 km/h in m/s
    val maxVolumePercent: Int = 60, // Default: 60% of device max volume
    val minVolumePercent: Int = 20, // Default: 20% of device max volume
    val speedUnit: SpeedUnit = SpeedUnit.KILOMETERS_PER_HOUR
)
