package com.rex50.tuneflow.domain.model

/**
 * Represents a user profile with custom volume and speed settings.
 *
 * @param id Unique identifier for the profile (0 for new profiles)
 * @param name Display name of the profile (max 20 characters)
 * @param colorHex Background color for the profile card in hex format (e.g., "#FF5722")
 * @param minSpeed Minimum speed threshold in m/s
 * @param maxSpeed Maximum speed threshold in m/s
 * @param minVolumePercent Minimum volume percentage (0-100)
 * @param maxVolumePercent Maximum volume percentage (0-100)
 * @param speedUnit Unit to display speed values
 */
data class Profile(
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val minSpeed: Float,
    val maxSpeed: Float,
    val minVolumePercent: Int,
    val maxVolumePercent: Int,
    val speedUnit: SpeedUnit
) {
    companion object {
        /**
         * Predefined colors for profile selection (Material3 themed)
         */
        val PRESET_COLORS = listOf(
            "#6750A4",
            "#7D5260",
            "#B3261E",
            "#E8871E",
            "#E8DEF8",
            "#5EEB5B",
            "#2E294E"
        )

        /**
         * Default values for City profile
         */
        fun createCityProfile(): Profile = Profile(
            id = 0,
            name = "City Drive",
            colorHex = PRESET_COLORS[0],
            minSpeed = 1.39f, // 5 km/h
            maxSpeed = 16.67f, // 60 km/h
            minVolumePercent = 20,
            maxVolumePercent = 50,
            speedUnit = SpeedUnit.KILOMETERS_PER_HOUR
        )

        /**
         * Default values for Highway profile
         */
        fun createHighwayProfile(): Profile = Profile(
            id = 0,
            name = "Highway Drive",
            colorHex = PRESET_COLORS[1],
            minSpeed = 16.67f, // 60 km/h
            maxSpeed = 33.33f, // 120 km/h
            minVolumePercent = 30,
            maxVolumePercent = 70,
            speedUnit = SpeedUnit.KILOMETERS_PER_HOUR
        )

        /**
         * Default values for Cycling profile
         */
        fun createCyclingProfile(): Profile = Profile(
            id = 0,
            name = "Cycling",
            colorHex = PRESET_COLORS[2],
            minSpeed = 2.78f, // 10 km/h
            maxSpeed = 8.33f, // 30 km/h
            minVolumePercent = 15,
            maxVolumePercent = 40,
            speedUnit = SpeedUnit.KILOMETERS_PER_HOUR
        )

        /**
         * Default values for Running profile
         */
        fun createRunningProfile(): Profile = Profile(
            id = 0,
            name = "Running",
            colorHex = PRESET_COLORS[3],
            minSpeed = 1.39f, // 5 km/h
            maxSpeed = 4.17f, // 15 km/h
            minVolumePercent = 10,
            maxVolumePercent = 30,
            speedUnit = SpeedUnit.KILOMETERS_PER_HOUR
        )
    }
}

