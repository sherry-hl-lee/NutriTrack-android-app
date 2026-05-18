package com.example.ass2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ass2.ui.components.HomeButton
import com.example.ass2.util.DateUtils
import com.example.ass2.viewmodel.MealViewModel
import com.example.ass2.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    mealViewModel: MealViewModel,
    userViewModel: UserViewModel
) {

    val meals by mealViewModel.meals.collectAsState(initial = emptyList())
    val todayMeals = meals.filter { DateUtils.isToday(it.date) }
    val totalCalories = todayMeals.sumOf { it.calories }
    val target by userViewModel.targetCalories.collectAsState()
    val isGuest by userViewModel.isGuest.collectAsState()
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(16.dp)
    ) {

        Text(
            "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = green
        )

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                Text("Today's Calories", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(8.dp))

                Text(
                    "$totalCalories / $target kcal",
                    color = green,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = (totalCalories.toFloat() / target).coerceAtMost(1f),
                    modifier = Modifier.fillMaxWidth(),
                    color = green
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        HomeButton("Add Meal", green) {
            if (isGuest) {
                showLoginRequiredDialog = true
            } else {
                navController.navigate("add")
            }
        }

        HomeButton("Search Food", green) {
            navController.navigate("search")
        }

        HomeButton("Reminder timer", green) {
            navController.navigate("Reminder")
        }

        HomeButton("Insights", green) {
            navController.navigate("insights")
        }

        HomeButton("Registered Users", green) {
            navController.navigate("users")
        }
    }

    if (showLoginRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showLoginRequiredDialog = false },
            title = { Text("Login required") },
            text = {
                Text("Please register or log in to use this feature.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLoginRequiredDialog = false
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Log in", color = green)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginRequiredDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

