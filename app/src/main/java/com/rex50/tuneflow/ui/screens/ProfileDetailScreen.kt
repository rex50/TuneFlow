package com.rex50.tuneflow.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    // Handle navigation on save/delete
    LaunchedEffect(uiState.isSaved, uiState.isDeleted) {
        Log.d(">>>>", "ProfileDetailScreen: ${uiState.isSaved}, ${uiState.isDeleted}")
        if (uiState.isSaved || uiState.isDeleted) {
            Toast.makeText(
                context,
                if (uiState.isSaved) "Profile saved successfully" else "Profile deleted successfully",
                Toast.LENGTH_SHORT
            ).show()
            onNavigateBack()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ProfileHeader(
                isNewProfile = uiState.isNewProfile,
                onNavigateBack = onNavigateBack,
                viewModel = viewModel,
                isLoading = uiState.isLoading
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    // Profile Name TextField
                    ProfileNameField(
                        value = profile.name,
                        onUpdateName = { viewModel.updateName(it) },
                        isLoading = uiState.isLoading,
                        error = uiState.nameError
                    )
                }

                item {
                    // Color Selector
                    ColorSelector(
                        selectedColor = profile.colorHex,
                        onColorSelected = { viewModel.updateColor(it) }
                    )
                }

                item {
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
                }

                item {
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
                }

                item {
                    // Volume Range Card
                    VolumeRangeCard(
                        minVolume = profile.minVolumePercent,
                        maxVolume = profile.maxVolumePercent,
                        onMinChange = { viewModel.updateMinVolume(it) },
                        onMaxChange = { viewModel.updateMaxVolume(it) }
                    )
                }

                item {
                    // Unit Selector Card
                    UnitSelectorCard(
                        selectedUnit = profile.speedUnit,
                        onUnitSelected = { viewModel.updateSpeedUnit(it) }
                    )
                }

                // Delete Button (only for existing profiles)
                if (profile.id > 0) {
                    item {
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
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
