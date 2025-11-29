package com.rex50.tuneflow.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.domain.model.Profile

@Composable
fun ProfilesList(
    profiles: List<Profile>,
    selectedProfile: Profile?,
    onProfileClicked: (Profile) -> Unit,
    onNavigateToProfileDetail: (Long) -> Unit
) {
    if (profiles.isNotEmpty()) {
        Text(
            text = "Profiles",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )

        profiles.forEach { profile ->
            ProfileCard(
                profile = profile,
                isSelected = selectedProfile?.id == profile.id,
                onCardClick = {
                    onProfileClicked(profile)
                },
                onEditClick = {
                    onNavigateToProfileDetail(profile.id)
                }
            )
        }
    }
}