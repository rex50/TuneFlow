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
     * Convert from m/s² to this unit's representation
     */
    fun convertFromMps2(valueInMps2: Float): Float = when (this) {
        METERS_PER_SECOND_SQUARED -> valueInMps2
        KILOMETERS_PER_HOUR -> valueInMps2 * 3.6f // m/s to km/h
        MILES_PER_HOUR -> valueInMps2 * 2.23694f // m/s to mph
    }

    /**
     * Convert from this unit to m/s²
     */
    fun convertToMps2(value: Float): Float = when (this) {
        METERS_PER_SECOND_SQUARED -> value
        KILOMETERS_PER_HOUR -> value / 3.6f
        MILES_PER_HOUR -> value / 2.23694f
    }

    companion object {
        fun fromOrdinal(ordinal: Int): AccelerationUnit =
            entries.getOrNull(ordinal) ?: METERS_PER_SECOND_SQUARED

        fun getMinRange(unit: AccelerationUnit): Float = 0f

        fun getMaxRangeForMin(unit: AccelerationUnit): Float = when (unit) {
            METERS_PER_SECOND_SQUARED -> 5f
            KILOMETERS_PER_HOUR -> 18f // ~5 m/s
            MILES_PER_HOUR -> 11.2f // ~5 m/s
        }

        fun getMaxRangeForMax(unit: AccelerationUnit): Float = when (unit) {
            METERS_PER_SECOND_SQUARED -> 20f
            KILOMETERS_PER_HOUR -> 72f // ~20 m/s
            MILES_PER_HOUR -> 44.7f // ~20 m/s
        }

        fun getMinRangeForMax(unit: AccelerationUnit): Float = when (unit) {
            METERS_PER_SECOND_SQUARED -> 5f
            KILOMETERS_PER_HOUR -> 18f
            MILES_PER_HOUR -> 11.2f
        }
    }
}

data class VolumeSettings(
    val isServiceEnabled: Boolean = false,
    val maxAcceleration: Float = 5.0f,
    val minAcceleration: Float = 0.1f,
    val maxVolume: Int = 100,
    val minVolume: Int = 0,
    val accelerationUnit: AccelerationUnit = AccelerationUnit.METERS_PER_SECOND_SQUARED
)
