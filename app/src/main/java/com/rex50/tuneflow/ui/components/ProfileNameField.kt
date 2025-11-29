package com.rex50.tuneflow.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Text field for entering a profile name with validation feedback.
 */
@Composable
fun ProfileNameField(
    value: String,
    onUpdateName: (String) -> Unit,
    isLoading: Boolean,
    error: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onUpdateName(it) },
        label = { Text("Profile Name") },
        placeholder = { Text("e.g., City, Highway") },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("Max 20 characters, must be unique")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading
    )
}
