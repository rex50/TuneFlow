package com.rex50.tuneflow.domain.model

data class VolumeSettings(
    val isServiceEnabled: Boolean = false,
    val maxAcceleration: Float = 5.0f,
    val minAcceleration: Float = 0.1f,
    val maxVolume: Int = 100,
    val minVolume: Int = 0
)
