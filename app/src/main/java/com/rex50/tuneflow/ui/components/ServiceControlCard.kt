package com.rex50.tuneflow.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R

/**
 * Displays a card UI for controlling the service state.
 *
 * Features:
 * - Shows service status (active/inactive)
 * - Provides a toggle switch to enable/disable the service
 * - Shows a helper text for user guidance
 *
 * @param isServiceEnabled Current service enabled state
 * @param title Title text for the card
 * @param onToggle Callback when the switch is toggled
 * @param modifier Optional modifier for the card
 */
@Composable
fun ServiceControlCard(
    isServiceEnabled: Boolean,
    title: String,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate top padding based on service state
    val padding by animateDpAsState(if (!isServiceEnabled) 16.dp else 0.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = padding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        // Status row with text and switch
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.service_control_helper),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Switch(checked = isServiceEnabled, onCheckedChange = onToggle)
        }
    }
}

/**
 * Preview for ServiceControlCard composable.
 */
@Preview(showBackground = true)
@Composable
fun ServiceControlCardPreview() {
    ServiceControlCard(
        isServiceEnabled = true,
        onToggle = {},
        modifier = Modifier
    )
}
