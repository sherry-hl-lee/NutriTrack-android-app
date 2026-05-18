package com.example.ass2.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ass2.reminder.ReminderPreferences
import com.example.ass2.reminder.ReminderScheduler
import com.example.ass2.util.ReminderTimeUtils
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = remember { ReminderPreferences(context) }

    val savedOnLoad = remember {
        if (prefs.isActive) prefs.hour to prefs.minute else null
    }

    val defaultCalendar = remember { Calendar.getInstance() }
    val state = rememberTimePickerState(
        initialHour = savedOnLoad?.first ?: defaultCalendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = savedOnLoad?.second ?: defaultCalendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    var isReminderSaved by remember { mutableStateOf(prefs.isActive) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val savedTimeLabel = if (isReminderSaved) {
        ReminderTimeUtils.formatTime12Hour(prefs.hour, prefs.minute)
    } else {
        null
    }
    val countdownText = if (isReminderSaved) {
        ReminderTimeUtils.formatCountdown(prefs.hour, prefs.minute, currentTime)
    } else {
        null
    }

    fun onReminderSaved() {
        isReminderSaved = true
        Toast.makeText(context, "Daily meal alert set!", Toast.LENGTH_SHORT).show()
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            saveAndScheduleReminder(context, prefs, state.hour, state.minute, ::onReminderSaved)
        } else {
            Toast.makeText(
                context,
                "Allow notifications so NutriTrack can remind you to log meals.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(isReminderSaved) {
        if (!isReminderSaved) return@LaunchedEffect
        currentTime = System.currentTimeMillis()
        while (isReminderSaved) {
            delay(1_000)
            currentTime = System.currentTimeMillis()
        }
    }

    fun persistReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (granted) {
                saveAndScheduleReminder(context, prefs, state.hour, state.minute, ::onReminderSaved)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            saveAndScheduleReminder(context, prefs, state.hour, state.minute, ::onReminderSaved)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(20.dp)
    ) {

        Text(
            "Daily meal reminder",
            style = MaterialTheme.typography.headlineMedium,
            color = green
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "One daily nudge to log your meals and stay on track.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(state = state)
            }
        }

        if (isReminderSaved && savedTimeLabel != null && countdownText != null) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Next meal check-in in: $countdownText",
                style = MaterialTheme.typography.bodyLarge,
                color = green
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "We'll remind you to log your meal at $savedTimeLabel every day.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { persistReminder() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green)
        ) {
            Text("Set daily meal alert")
        }

        if (isReminderSaved) {
            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    ReminderScheduler.cancel(context)
                    prefs.clearReminder()
                    isReminderSaved = false
                    Toast.makeText(context, "Daily meal alert turned off", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text("Turn off daily alert", color = Color.Red)
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, green)
        ) {
            Text("Back", color = green)
        }
    }
}

private fun saveAndScheduleReminder(
    context: android.content.Context,
    prefs: ReminderPreferences,
    hour: Int,
    minute: Int,
    onSaved: () -> Unit
) {
    prefs.saveReminder(hour, minute)
    ReminderScheduler.schedule(context, hour, minute)
    onSaved()
}
