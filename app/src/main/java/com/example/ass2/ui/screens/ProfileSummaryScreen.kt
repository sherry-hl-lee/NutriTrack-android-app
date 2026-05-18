package com.example.ass2.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2.ui.components.BmiChart
import com.example.ass2.util.ProfileHealthCalculator
import com.example.ass2.viewmodel.UserViewModel

@Composable
fun ProfileSummaryScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    LaunchedEffect(Unit) {
        userViewModel.refreshCurrentUserFromDb()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            "My Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = green,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (currentUser == null) {
                Text("No profile data. Please log in and save your profile.", color = Color.Gray)
            } else {
                val user = currentUser!!

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileInfoRow("Email", user.email)
                        ProfileInfoRow("Weight", "${user.weight} kg")
                        ProfileInfoRow("Height", "${user.height} cm")
                        ProfileInfoRow("Age", user.age.toString())
                        ProfileInfoRow("Gender", user.gender.ifBlank { "-" }, isLast = true)
                    }
                }

                val health = ProfileHealthCalculator.calculate(
                    weightKg = user.weight,
                    heightCm = user.height,
                    age = user.age,
                    gender = user.gender
                )

                if (health != null) {
                    Spacer(Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Health Summary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = green,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            BmiChart(
                                bmi = health.bmi,
                                weightStatus = health.weightStatus,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            ProfileInfoRow(
                                "Recommended daily intake",
                                "${health.recommendedDailyCalories} kcal",
                                isLast = true
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = {
                navController.navigate("profile") {
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green)
        ) {
            Text("Edit Profile")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Text("Log out")
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.logout()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        showLogoutDialog = false
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Confirm", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    val green = Color(0xFF4CAF50)

    Text(
        label,
        style = MaterialTheme.typography.labelMedium,
        color = Color.Gray
    )
    Text(
        value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = green,
        modifier = Modifier.padding(bottom = if (isLast) 0.dp else 8.dp)
    )
}
