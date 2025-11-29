package com.rex50.tuneflow.data

@Deprecated("Use domain model com.rex50.tuneflow.domain.model.VolumeSettings")
data class VolumeSettings(
    val minVolume: Int = 5,
    val maxVolume: Int = 15,
    val minSpeed: Float = 0f,
    val maxSpeed: Float = 10f,
    val isServiceEnabled: Boolean = false
)
