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

/**
 * Displays a card showing how acceleration maps to volume.
 *
 * Features:
 * - Shows mapping for minimum and maximum acceleration/volume
 * - Explains how the mapping works
 * - Provides helper text for user guidance
 *
 * @param minAcceleration Minimum acceleration value
 * @param maxAcceleration Maximum acceleration value
 * @param minVolume Minimum volume value
 * @param maxVolume Maximum volume value
 * @param modifier Optional modifier for the card
 */
@Composable
fun VolumeMappingCard(
    minAcceleration: Float,
    maxAcceleration: Float,
    minVolume: Int,
    maxVolume: Int,
    modifier: Modifier = Modifier
) {
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
                text = stringResource(R.string.volume_mapping_min, minAcceleration, minVolume),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.volume_mapping_max, maxAcceleration, maxVolume),
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
fun VolumeMappingCardPreview() {
    VolumeMappingCard(
        minAcceleration = 0f,
        maxAcceleration = 10f,
        minVolume = 0,
        maxVolume = 15
    )
}
