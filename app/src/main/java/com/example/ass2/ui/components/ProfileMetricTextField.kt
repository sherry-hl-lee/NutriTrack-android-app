package com.example.ass2.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProfileMetricTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = errorMessage != null,
        supportingText = errorMessage?.let { message ->
            {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}
