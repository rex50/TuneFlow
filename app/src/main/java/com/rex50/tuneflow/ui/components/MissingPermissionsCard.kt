package com.rex50.tuneflow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.R
import com.rex50.tuneflow.domain.model.PermissionStatus
import com.rex50.tuneflow.domain.model.PermissionType

/**
 * Displays a card showing missing permissions with inline action buttons.
 *
 * Features:
 * - Lists each missing permission/GPS requirement
 * - Shows descriptive icon, title, and description for each
 * - Provides "Allow" button if permission can be requested
 * - Shows "Open Settings" button if permission was denied
 * - Matches existing card styling (primaryContainer)
 *
 * @param missingRequirements List of permission statuses that are not granted
 * @param onActionClick Callback when user taps action button for a permission
 * @param modifier Optional modifier for the card
 */
@Composable
fun MissingPermissionsCard(
    missingRequirements: List<PermissionStatus>,
    onActionClick: (PermissionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = stringResource(R.string.permissions_required_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            // Helper text
            Text(
                text = stringResource(R.string.permissions_required_helper),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            )

            // List of missing requirements
            missingRequirements.forEach { requirement ->
                PermissionRow(
                    requirement = requirement,
                    onActionClick = { onActionClick(requirement.type) }
                )
            }
        }
    }
}

/**
 * Single row displaying a permission requirement with action button
 */
@Composable
private fun PermissionRow(
    requirement: PermissionStatus,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = when (requirement.type) {
                PermissionType.GPS_ENABLED -> Icons.Default.LocationOn
                PermissionType.POST_NOTIFICATIONS -> Icons.Default.Notifications
                else -> Icons.Default.LocationOn
            },
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onErrorContainer
        )

        // Title and description
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(requirement.titleRes),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = stringResource(requirement.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            )
        }

        // Action button
        Button(onClick = onActionClick) {
            Text(
                text = if (requirement.canRequest) {
                    stringResource(R.string.permission_button_allow)
                } else {
                    stringResource(R.string.permission_button_settings)
                }
            )
        }
    }
}

/**
 * Preview for MissingPermissionsCard composable
 */
@Preview(showBackground = true)
@Composable
private fun MissingPermissionsCardPreview() {
    val sampleRequirements = listOf(
        PermissionStatus(
            type = PermissionType.FINE_LOCATION,
            isGranted = false,
            canRequest = true,
            titleRes = R.string.permission_fine_location_title,
            descriptionRes = R.string.permission_fine_location_description
        ),
        PermissionStatus(
            type = PermissionType.GPS_ENABLED,
            isGranted = false,
            canRequest = true,
            titleRes = R.string.permission_gps_title,
            descriptionRes = R.string.permission_gps_description
        )
    )

    MissingPermissionsCard(
        missingRequirements = sampleRequirements,
        onActionClick = {}
    )
}

