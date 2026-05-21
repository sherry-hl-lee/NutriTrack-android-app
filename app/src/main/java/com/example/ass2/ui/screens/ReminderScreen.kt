package com.example.ass2.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ass2.data.local.UserReminder
import com.example.ass2.reminder.ReminderType
import com.example.ass2.util.ReminderTimeUtils
import com.example.ass2.viewmodel.ReminderViewModel
import com.example.ass2.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val user by userViewModel.currentUser.collectAsState()
    val isGuest by userViewModel.isGuest.collectAsState()
    val reminders by reminderViewModel.reminders.collectAsState()

    val defaultCalendar = remember { Calendar.getInstance() }
    val timeState = rememberTimePickerState(
        initialHour = defaultCalendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = defaultCalendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    var selectedType by remember { mutableStateOf(ReminderType.WATER) }
    var typeExpanded by remember { mutableStateOf(false) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(user?.email) {
        reminderViewModel.setUserEmail(user?.email)
        if (user?.email != null) {
            reminderViewModel.rescheduleForCurrentUser(context)
        }
    }

    LaunchedEffect(reminders) {
        if (reminders.any { it.enabled }) {
            while (true) {
                delay(1_000)
                nowMillis = System.currentTimeMillis()
            }
        }
    }

    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingAction?.invoke()
        } else {
            Toast.makeText(
                context,
                "Allow notifications to receive reminders.",
                Toast.LENGTH_LONG
            ).show()
        }
        pendingAction = null
    }

    fun runWithNotificationPermission(action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (granted) {
                action()
            } else {
                pendingAction = action
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            action()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            "Meal & hydration reminders",
            style = MaterialTheme.typography.headlineMedium,
            color = green,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Each account keeps its own alarms. Add water, breakfast, lunch, or dinner reminders.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        when {
            isGuest || user == null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        "Sign in to save personal reminders. Guest mode cannot store alarms.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.DarkGray
                    )
                }
            }

            else -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Add reminder", fontWeight = FontWeight.SemiBold, color = green)

                        Spacer(Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedType.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Reminder type") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = green,
                                    unfocusedIndicatorColor = green
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                ReminderType.entries.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.label) },
                                        onClick = {
                                            selectedType = type
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        TimePicker(state = timeState)

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                runWithNotificationPermission {
                                    reminderViewModel.addReminder(
                                        context,
                                        selectedType,
                                        timeState.hour,
                                        timeState.minute
                                    ) { ok ->
                                        if (ok) {
                                            Toast.makeText(
                                                context,
                                                "${selectedType.label} reminder saved",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = green)
                        ) {
                            Text("Save reminder")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Your Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    color = green,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                if (reminders.isEmpty()) {
                    Text(
                        "No reminders yet. Add one above.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    reminders.forEach { reminder ->
                        ReminderListItem(
                            reminder = reminder,
                            nowMillis = nowMillis,
                            green = green,
                            onToggle = { enabled ->
                                runWithNotificationPermission {
                                    reminderViewModel.setReminderEnabled(context, reminder, enabled)
                                }
                            },
                            onDelete = {
                                reminderViewModel.deleteReminder(context, reminder)
                                Toast.makeText(context, "Reminder removed", Toast.LENGTH_SHORT).show()
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, green)
        ) {
            Text("Back", color = green)
        }
    }
}

@Composable
private fun ReminderListItem(
    reminder: UserReminder,
    nowMillis: Long,
    green: Color,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val type = ReminderType.fromId(reminder.type)
    val label = type?.label ?: reminder.type
    val timeLabel = ReminderTimeUtils.formatTime12Hour(reminder.hour, reminder.minute)
    val countdown = if (reminder.enabled) {
        ReminderTimeUtils.formatCountdown(reminder.hour, reminder.minute, nowMillis)
    } else {
        "Off"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, fontWeight = FontWeight.SemiBold, color = green)
                    Text("Daily at $timeLabel", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    if (reminder.enabled) {
                        Text(
                            "Next in: $countdown",
                            style = MaterialTheme.typography.bodyMedium,
                            color = green
                        )
                    }
                }
                Switch(
                    checked = reminder.enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = green
                    )
                )
            }
            TextButton(onClick = onDelete) {
                Text("Delete", color = Color.Red)
            }
        }
    }
}
