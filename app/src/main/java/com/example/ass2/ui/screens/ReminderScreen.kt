package com.example.ass2.ui.screens

import android.widget.Toast
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
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavController) {

    val state = rememberTimePickerState()

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val hour12 = if (state.hour % 12 == 0) 12 else state.hour % 12
    val amPm = if (state.hour < 12) "AM" else "PM"
    val context = LocalContext.current
    val formattedTime = String.format(
        "%02d:%02d %s",
        hour12,
        state.minute,
        amPm
    )

    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
    val now = currentTime
    val calendar = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, state.hour)
        set(java.util.Calendar.MINUTE, state.minute)
        set(java.util.Calendar.SECOND, 0)
    }
    if (calendar.timeInMillis <= now) {
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
    }
    val diffMillis = calendar.timeInMillis - now
    val totalMinutes = diffMillis / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(20.dp)
    ) {

        Text(
            "Reminder timer",
            style = MaterialTheme.typography.headlineMedium,
            color = green
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
                contentAlignment = Alignment.Center   // ✅ 核心！
            ) {
                TimePicker(state = state)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Reminder set for: $formattedTime",
            style = MaterialTheme.typography.bodyLarge,
            color = green
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "⏳ Countdown: ${hours}h ${minutes}min",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Reminder set successful!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green)
        ) {
            Text("Save Reminder")
        }

        Spacer(Modifier.height(16.dp))

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