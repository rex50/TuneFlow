package com.rex50.tuneflow.data

data class VolumeSettings(
    val minVolume: Int = 5,
    val maxVolume: Int = 15,
    val minAcceleration: Float = 0f,
    val maxAcceleration: Float = 10f,
    val isServiceEnabled: Boolean = false
)

