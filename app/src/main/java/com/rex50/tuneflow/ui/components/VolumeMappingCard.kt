package com.rex50.tuneflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.ui.theme.TuneFlowTheme

/**
 * Displays a card showing how speed maps to volume.
 *
 * Features:
 * - Shows mapping for minimum and maximum speed/volume in selected unit
 * - Explains how the mapping works
 * - Provides helper text for user guidance
 *
 * @param volumeSettings Current volume settings including speed unit
 * @param modifier Optional modifier for the card
 */
@Composable
fun VolumeMappingCard(
    volumeSettings: VolumeSettings,
    modifier: Modifier = Modifier
) {
    val unit = volumeSettings.speedUnit
    val minDisplayValue = unit.convertFromMps(volumeSettings.minSpeed)
    val maxDisplayValue = unit.convertFromMps(volumeSettings.maxSpeed)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.volume_mapping_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "- At %.1f %s: Volume = %d%%".format(minDisplayValue, unit.getLabel(), volumeSettings.minVolumePercent),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "- At %.1f %s: Volume = %d%%".format(maxDisplayValue, unit.getLabel(), volumeSettings.maxVolumePercent),
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.volume_mapping_how_it_works),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(R.string.volume_mapping_helper),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Preview for VolumeMappingCard composable.
 */
@Preview(showBackground = true)
@Composable
private fun VolumeMappingCardPreview() {
    VolumeMappingCard(
        volumeSettings = VolumeSettings(
            minSpeed = 0f,
            maxSpeed = 10f,
            minVolumePercent = 20,
            maxVolumePercent = 60
        )
    )
}
