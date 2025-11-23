package com.rex50.tuneflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.rex50.tuneflow.data.PreferencesManager
import com.rex50.tuneflow.data.VolumeSettings
import com.rex50.tuneflow.service.VolumeControlService
import com.rex50.tuneflow.ui.theme.TuneFlowTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, can start service if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            TuneFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VolumeControlScreen(preferencesManager)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeControlScreen(preferencesManager: PreferencesManager) {
    val volumeSettings by preferencesManager.volumeSettings.collectAsState(initial = VolumeSettings())
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TuneFlow") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Service Control Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Service Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (volumeSettings.isServiceEnabled) "Service Active" else "Service Inactive",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Switch(
                            checked = volumeSettings.isServiceEnabled,
                            onCheckedChange = { enabled ->
                                scope.launch {
                                    preferencesManager.updateServiceEnabled(enabled)
                                    if (enabled) {
                                        VolumeControlService.startService(context)
                                    } else {
                                        VolumeControlService.stopService(context)
                                    }
                                }
                            }
                        )
                    }

                    Text(
                        text = "Enable to start monitoring acceleration and adjusting volume",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // Volume Range Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Volume Range",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Min Volume
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Minimum Volume")
                            Text(
                                text = volumeSettings.minVolume.toString(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = volumeSettings.minVolume.toFloat(),
                            onValueChange = { value ->
                                scope.launch {
                                    preferencesManager.updateMinVolume(value.toInt())
                                }
                            },
                            valueRange = 0f..15f,
                            steps = 14
                        )
                    }

                    // Max Volume
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Maximum Volume")
                            Text(
                                text = volumeSettings.maxVolume.toString(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = volumeSettings.maxVolume.toFloat(),
                            onValueChange = { value ->
                                scope.launch {
                                    preferencesManager.updateMaxVolume(value.toInt())
                                }
                            },
                            valueRange = 0f..15f,
                            steps = 14
                        )
                    }

                    Text(
                        text = "Set the minimum and maximum media volume levels",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Acceleration Range Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Acceleration Range",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Min Acceleration
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Minimum Acceleration")
                            Text(
                                text = "%.1f m/s²".format(volumeSettings.minAcceleration),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = volumeSettings.minAcceleration,
                            onValueChange = { value ->
                                scope.launch {
                                    preferencesManager.updateMinAcceleration(value)
                                }
                            },
                            valueRange = 0f..5f,
                            steps = 49
                        )
                    }

                    // Max Acceleration
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Maximum Acceleration")
                            Text(
                                text = "%.1f m/s²".format(volumeSettings.maxAcceleration),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = volumeSettings.maxAcceleration,
                            onValueChange = { value ->
                                scope.launch {
                                    preferencesManager.updateMaxAcceleration(value)
                                }
                            },
                            valueRange = 5f..20f,
                            steps = 149
                        )
                    }

                    Text(
                        text = "Define the acceleration range that maps to your volume range",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Volume Mapping Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Volume Mapping",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "• At %.1f m/s²: Volume = %d".format(
                            volumeSettings.minAcceleration,
                            volumeSettings.minVolume
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "• At %.1f m/s²: Volume = %d".format(
                            volumeSettings.maxAcceleration,
                            volumeSettings.maxVolume
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "How it works:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "The app continuously monitors your device's acceleration. As acceleration increases (e.g., when driving faster or on rough roads), the volume automatically adjusts within your specified range.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}