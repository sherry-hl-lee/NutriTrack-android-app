package com.example.ass2.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MealReminderAlertDialog(
    onDismiss: () -> Unit
) {
    val green = Color(0xFF4CAF50)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Time to log your meal") },
        text = {
            Text("Open NutriTrack to add today's meal and check your calories.")
        },
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
