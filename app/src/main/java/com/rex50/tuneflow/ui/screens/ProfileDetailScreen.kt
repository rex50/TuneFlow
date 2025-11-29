package com.rex50.tuneflow.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.ui.components.*
import com.rex50.tuneflow.ui.viewmodel.ProfileDetailViewModel

/**
 * Screen for creating or editing a profile
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile

    // Handle navigation on save/delete
    LaunchedEffect(uiState.isSaved, uiState.isDeleted) {
        Log.d(">>>>", "ProfileDetailScreen: ${uiState.isSaved}, ${uiState.isDeleted}")
        if (uiState.isSaved || uiState.isDeleted) {
            onNavigateBack()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (profile?.id == 0L) "New Profile" else "Edit Profile"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Save button
                    Button(
                        onClick = { viewModel.saveProfile() },
                        enabled = !uiState.isLoading
                    ) {
                        Text(text = "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (profile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Name TextField
                OutlinedTextField(
                    value = profile.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Profile Name") },
                    placeholder = { Text("e.g., City, Highway") },
                    isError = uiState.nameError != null,
                    supportingText = {
                        val errorMsg = uiState.nameError
                        if (errorMsg != null) {
                            Text(
                                text = errorMsg,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Max 20 characters, must be unique")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                // Color Selector
                ColorSelector(
                    selectedColor = profile.colorHex,
                    onColorSelected = { viewModel.updateColor(it) }
                )

                // Volume Mapping Card
                VolumeMappingCard(
                    volumeSettings = VolumeSettings(
                        minSpeed = profile.minSpeed,
                        maxSpeed = profile.maxSpeed,
                        speedUnit = profile.speedUnit,
                        minVolumePercent = profile.minVolumePercent,
                        maxVolumePercent = profile.maxVolumePercent
                    )
                )

                // Speed Range Card
                SpeedRangeCard(
                    volumeSettings = VolumeSettings(
                        minSpeed = profile.minSpeed,
                        maxSpeed = profile.maxSpeed,
                        speedUnit = profile.speedUnit,
                        minVolumePercent = profile.minVolumePercent,
                        maxVolumePercent = profile.maxVolumePercent
                    ),
                    onMinChange = { viewModel.updateMinSpeed(it) },
                    onMaxChange = { viewModel.updateMaxSpeed(it) }
                )

                // Volume Range Card
                VolumeRangeCard(
                    minVolume = profile.minVolumePercent,
                    maxVolume = profile.maxVolumePercent,
                    onMinChange = { viewModel.updateMinVolume(it) },
                    onMaxChange = { viewModel.updateMaxVolume(it) }
                )

                // Unit Selector Card
                UnitSelectorCard(
                    selectedUnit = profile.speedUnit,
                    onUnitSelected = { viewModel.updateSpeedUnit(it) }
                )

                // Delete Button (only for existing profiles)
                if (profile.id > 0) {
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        Text("Delete Profile")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Profile?") },
            text = { Text("Are you sure you want to delete this profile? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteProfile()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

