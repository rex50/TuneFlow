package com.rex50.tuneflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.SpeedUnit
import com.rex50.tuneflow.domain.model.VolumeSettings

/**
 * Displays a card UI for configuring the minimum and maximum speed range.
 *
 * Features:
 * - Shows sliders for min and max speed values in selected unit
 * - Displays current values with labels
 * - Provides helper text for user guidance
 * - Automatically converts between units
 *
 * @param volumeSettings Current volume settings including speed unit
 * @param onMinChange Callback when min speed slider changes (in m/s)
 * @param onMaxChange Callback when max speed slider changes (in m/s)
 * @param modifier Optional modifier for the card
 */
@Composable
fun SpeedRangeCard(
    volumeSettings: VolumeSettings,
    onMinChange: (Float) -> Unit,
    onMaxChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val unit = volumeSettings.speedUnit
    val minDisplayValue = unit.convertFromMps(volumeSettings.minSpeed)
    val maxDisplayValue = unit.convertFromMps(volumeSettings.maxSpeed)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.speed_range_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.speed_min_label))
                    Text(
                        text = "%.1f %s".format(minDisplayValue, unit.getLabel()),
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = minDisplayValue,
                    onValueChange = { displayValue ->
                        // Convert back to m/s before calling callback
                        onMinChange(unit.convertToMps(displayValue))
                    },
                    valueRange = SpeedUnit.getMinRange(unit)..SpeedUnit.getMaxRangeForMin(unit),
                    steps = 49
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.speed_max_label))
                    Text(
                        text = "%.1f %s".format(maxDisplayValue, unit.getLabel()),
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = maxDisplayValue,
                    onValueChange = { displayValue ->
                        // Convert back to m/s before calling callback
                        onMaxChange(unit.convertToMps(displayValue))
                    },
                    valueRange = SpeedUnit.getMinRangeForMax(unit)..SpeedUnit.getMaxRangeForMax(unit),
                    steps = 149
                )
            }

            Text(
                text = stringResource(R.string.speed_range_helper),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Preview for SpeedRangeCard composable.
 */
@Preview(showBackground = true)
@Composable
private fun SpeedRangeCardPreview() {
    SpeedRangeCard(
        volumeSettings = VolumeSettings(
            minSpeed = 2.5f,
            maxSpeed = 10f
        ),
        onMinChange = {},
        onMaxChange = {}
    )
}
