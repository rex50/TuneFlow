package com.rex50.tuneflow.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut


data class NavigationTransitions(
    val enterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition,
    val exitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition,
    val popEnterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition,
    val popExitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition
)

object NavigationAnimations {
    private const val ANIMATION_DURATION = 300

    fun fadeSlideAnimation(
        durationMillis: Int = ANIMATION_DURATION
    ): NavigationTransitions {
        return NavigationTransitions(
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(durationMillis)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(durationMillis)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(durationMillis)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(durationMillis)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(durationMillis)
                        )
            }
        )
    }

}