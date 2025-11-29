package com.rex50.tuneflow.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the TuneFlow app.
 * Using Kotlin Serialization for compile-time safe navigation arguments.
 */

/**
 * Home screen route - no arguments required
 */
@Serializable
object Home

/**
 * Profile detail screen route for creating or editing a profile
 * @param profileId The ID of the profile to edit, or 0 for creating a new profile
 */
@Serializable
data class ProfileDetail(
    val profileId: Long = 0L
)

