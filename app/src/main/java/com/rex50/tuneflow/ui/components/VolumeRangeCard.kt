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
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R

@Composable
fun VolumeRangeCard(
    minVolume: Int,
    maxVolume: Int,
    onMinChange: (Int) -> Unit,
    onMaxChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.volume_range_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.volume_min_label))
                    Text(text = minVolume.toString(), fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = minVolume.toFloat(),
                    onValueChange = { onMinChange(it.toInt()) },
                    valueRange = 0f..15f,
                    steps = 14
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.volume_max_label))
                    Text(text = maxVolume.toString(), fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = maxVolume.toFloat(),
                    onValueChange = { onMaxChange(it.toInt()) },
                    valueRange = 0f..15f,
                    steps = 14
                )
            }

            Text(
                text = stringResource(R.string.volume_range_helper),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

