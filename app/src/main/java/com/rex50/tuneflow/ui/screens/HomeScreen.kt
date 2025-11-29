package com.rex50.tuneflow.ui.screens

import android.util.Log
import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.PermissionsUiState
import com.rex50.tuneflow.domain.model.Profile
import com.rex50.tuneflow.service.VolumeControlService
import com.rex50.tuneflow.ui.HomeScreenViewModel
import com.rex50.tuneflow.ui.components.MissingPermissionsCard
import com.rex50.tuneflow.ui.components.ProfileCard
import com.rex50.tuneflow.ui.components.ProfilesList
import com.rex50.tuneflow.ui.components.ServiceControlCard
import com.rex50.tuneflow.ui.components.SpeedometerCard
import com.rex50.tuneflow.ui.components.UnitSelectorCard
import com.rex50.tuneflow.ui.components.VolumeMappingCard
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
    val permissionsState by viewModel.permissionsUiState.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val isServiceEnabled by viewModel.isServiceEnabled.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val serviceState by viewModel.state.collectAsState()

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
            if(isServiceEnabled) {
                item {
                    // Small gauge showing current speed (matches existing styling: simple, clean)
                    selectedProfile?.let { profile ->
                        SpeedometerCard(
                            currentSpeed = serviceState.speed,
                            maxSpeed = 200f,
                            speedUnit = profile.speedUnit
                        )
                    }
                }
            }

            item {
                // Permission/Service Control Section
                when (val state = permissionsState) {
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
                            isServiceEnabled = isServiceEnabled,
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
            if (profiles.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Profiles",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        isSelected = selectedProfile?.id == profile.id,
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
