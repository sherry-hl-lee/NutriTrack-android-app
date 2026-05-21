package com.example.ass2.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MealReminderAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    val green = Color(0xFF4CAF50)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = green)
            ) {
                Text("Close")
            }
        }
    )
}
