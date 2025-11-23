package com.rex50.tuneflow.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R
import com.rex50.tuneflow.service.VolumeControlService
import com.rex50.tuneflow.ui.HomeScreenViewModel
import com.rex50.tuneflow.ui.components.AccelerationRangeCard
import com.rex50.tuneflow.ui.components.ServiceControlCard
import com.rex50.tuneflow.ui.components.SpeedometerCard
import com.rex50.tuneflow.ui.components.VolumeMappingCard
import com.rex50.tuneflow.ui.components.VolumeRangeCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel) {
    val volumeSettings by viewModel.volumeSettings.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val serviceState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Small gauge showing acceleration (matches existing styling: simple, clean)
            AnimatedVisibility(volumeSettings.isServiceEnabled) {
                SpeedometerCard(volumeSettings, serviceState)
            }

            // Service Control Section
            ServiceControlCard(
                isServiceEnabled = volumeSettings.isServiceEnabled,
                onToggle = { enabled ->
                    viewModel.setServiceEnabled(enabled)
                    scope.launch {
                        if (enabled) {
                            VolumeControlService.startService(context)
                        } else {
                            VolumeControlService.stopService(context)
                        }
                    }
                }
            )

            // Volume Range Section
            VolumeRangeCard(
                minVolume = volumeSettings.minVolume,
                maxVolume = volumeSettings.maxVolume,
                onMinChange = { viewModel.updateMinVolume(it) },
                onMaxChange = { viewModel.updateMaxVolume(it) }
            )

            // Acceleration Range Section
            AccelerationRangeCard(
                minAcceleration = volumeSettings.minAcceleration,
                maxAcceleration = volumeSettings.maxAcceleration,
                onMinChange = { viewModel.updateMinAcceleration(it) },
                onMaxChange = { viewModel.updateMaxAcceleration(it) }
            )

            // Volume Mapping Info
            VolumeMappingCard(
                minAcceleration = volumeSettings.minAcceleration,
                maxAcceleration = volumeSettings.maxAcceleration,
                minVolume = volumeSettings.minVolume,
                maxVolume = volumeSettings.maxVolume
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
