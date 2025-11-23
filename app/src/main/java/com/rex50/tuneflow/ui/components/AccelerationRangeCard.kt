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
import com.rex50.tuneflow.domain.model.AccelerationUnit
import com.rex50.tuneflow.domain.model.VolumeSettings
import com.rex50.tuneflow.ui.theme.TuneFlowTheme

/**
 * Displays a card UI for configuring the minimum and maximum acceleration range.
 *
 * Features:
 * - Shows sliders for min and max acceleration values in selected unit
 * - Displays current values with labels
 * - Provides helper text for user guidance
 * - Automatically converts between units
 *
 * @param volumeSettings Current volume settings including acceleration unit
 * @param onMinChange Callback when min acceleration slider changes (in m/s²)
 * @param onMaxChange Callback when max acceleration slider changes (in m/s²)
 * @param modifier Optional modifier for the card
 */
@Composable
fun AccelerationRangeCard(
    volumeSettings: VolumeSettings,
    onMinChange: (Float) -> Unit,
    onMaxChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val unit = volumeSettings.accelerationUnit
    val minDisplayValue = unit.convertFromMps2(volumeSettings.minAcceleration)
    val maxDisplayValue = unit.convertFromMps2(volumeSettings.maxAcceleration)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(
                    if(unit == AccelerationUnit.METERS_PER_SECOND_SQUARED)
                        R.string.acceleration_range_title
                    else
                        R.string.speed_range_title
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.acceleration_min_label))
                    Text(
                        text = "%.1f %s".format(minDisplayValue, unit.getLabel()),
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = minDisplayValue,
                    onValueChange = { displayValue ->
                        // Convert back to m/s² before calling callback
                        onMinChange(unit.convertToMps2(displayValue))
                    },
                    valueRange = AccelerationUnit.getMinRange(unit)..AccelerationUnit.getMaxRangeForMin(unit),
                    steps = 49
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.acceleration_max_label))
                    Text(
                        text = "%.1f %s".format(maxDisplayValue, unit.getLabel()),
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = maxDisplayValue,
                    onValueChange = { displayValue ->
                        // Convert back to m/s² before calling callback
                        onMaxChange(unit.convertToMps2(displayValue))
                    },
                    valueRange = AccelerationUnit.getMinRangeForMax(unit)..AccelerationUnit.getMaxRangeForMax(unit),
                    steps = 149
                )
            }

            Text(
                text = stringResource(R.string.acceleration_range_helper),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Preview for AccelerationRangeCard composable.
 */
@Preview(showBackground = true)
@Composable
private fun AccelerationRangeCardPreview() {
    AccelerationRangeCard(
        volumeSettings = VolumeSettings(
            minAcceleration = 2.5f,
            maxAcceleration = 10f
        ),
        onMinChange = {},
        onMaxChange = {}
    )
}
