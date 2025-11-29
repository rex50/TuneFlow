package com.rex50.tuneflow.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rex50.tuneflow.domain.model.Profile

@Composable
fun LazyListScope.ProfilesList(
    profiles: List<Profile>,
    selectedProfile: Profile?,
    onProfileClicked: (Profile) -> Unit,
    onNavigateToProfileDetail: (Long) -> Unit
) {

}