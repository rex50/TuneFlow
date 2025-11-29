package com.rex50.tuneflow.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.PermissionsUiState
import com.rex50.tuneflow.service.VolumeControlService
import com.rex50.tuneflow.ui.HomeScreenViewModel
import com.rex50.tuneflow.ui.components.MissingPermissionsCard
import com.rex50.tuneflow.ui.components.ProfileCard
import com.rex50.tuneflow.ui.components.ServiceControlCard
import com.rex50.tuneflow.ui.components.SpeedometerCard
import kotlinx.coroutines.launch

/**
 * Main home screen for the TuneFlow app.
 *
 * Displays:
 * - Missing permissions card (if any requirements not met)
 * - Speedometer gauge for acceleration
 * - Service control card
 * - Volume range configuration
 * - Acceleration range configuration
 * - Volume mapping information
 *
 * All sections are interactive and update via the provided [HomeScreenViewModel].
 *
 * @param viewModel The ViewModel providing UI state and actions
 * @param onNavigateToProfileDetail Callback for navigating to profile detail screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onNavigateToProfileDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isServiceEnabled) {
                item {
                    // Small gauge showing current speed (matches existing styling: simple, clean)
                    uiState.selectedProfile?.let { profile ->
                        SpeedometerCard(
                            currentSpeed = uiState.serviceState.speed,
                            maxSpeed = 200f,
                            speedUnit = profile.speedUnit
                        )
                    }
                }
            }

            item {
                // Permission/Service Control Section
                when (val state = uiState.permissionsUiState) {
                    is PermissionsUiState.MissingRequirements -> {
                        MissingPermissionsCard(
                            missingRequirements = state.requirements,
                            onActionClick = { permissionType ->
                                viewModel.onPermissionAction(permissionType)
                            }
                        )
                    }

                    PermissionsUiState.AllGranted -> {
                        ServiceControlCard(
                            isServiceEnabled = uiState.isServiceEnabled,
                            onToggle = { enabled ->
                                scope.launch {
                                    if (enabled) {
                                        VolumeControlService.startService(context)
                                    } else {
                                        VolumeControlService.stopService(context)
                                    }
                                }
                            }
                        )
                    }
                }
            }


            // Profiles Section
            if (uiState.profiles.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Profiles",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        isSelected = uiState.selectedProfile?.id == profile.id,
                        onCardClick = {
                            viewModel.selectProfile(profile.id)
                        },
                        onEditClick = {
                            Log.d(">>>>", "HomeScreen: onNavigateToProfileDetail")
                            onNavigateToProfileDetail(profile.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
