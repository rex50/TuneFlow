package com.rex50.tuneflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.rex50.tuneflow.ui.viewmodel.ProfileDetailViewModel

/**
 * Header bar for the profile detail screen with back navigation and save action.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileHeader(
    isNewProfile: Boolean,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: ProfileDetailViewModel,
) {
    TopAppBar(
        title = {
            Text(
                text = if (isNewProfile) "New Profile" else "Edit Profile"
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            // Save button
            Button(
                onClick = { viewModel.saveProfile() },
                enabled = !isLoading
            ) {
                Text(text = "Save")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}