package com.example.ass2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2.viewmodel.AiSuggestUiState
import com.example.ass2.viewmodel.AiSuggestViewModel
import com.example.ass2.viewmodel.MealViewModel
import com.example.ass2.viewmodel.UserViewModel

@Composable
fun AiSuggestScreen(
    navController: NavController,
    mealViewModel: MealViewModel,
    userViewModel: UserViewModel,
    aiSuggestViewModel: AiSuggestViewModel
) {
    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val meals by mealViewModel.meals.collectAsState(initial = emptyList())
    val user by userViewModel.currentUser.collectAsState()
    val target by userViewModel.targetCalories.collectAsState()
    val uiState by aiSuggestViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Daily nutrition feedback",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = green
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Get a clear daily score and practical food suggestions based on your profile and today's meals.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                aiSuggestViewModel.requestSuggestion(
                    user = user,
                    meals = meals,
                    targetCalories = target
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green),
            enabled = uiState !is AiSuggestUiState.Loading
        ) {
            Text("Generate score & feedback")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Back", color = green)
        }

        Spacer(Modifier.height(20.dp))

        when (val s = uiState) {
            AiSuggestUiState.Idle -> {
                Text(
                    "Tap \"Generate score & feedback\" to view today's evaluation.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            AiSuggestUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = green)
                    Spacer(Modifier.height(8.dp))
                    Text("Thinking…", color = Color.Gray)
                }
            }

            is AiSuggestUiState.Error -> {
                Text(
                    s.message,
                    color = Color(0xFFC62828),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is AiSuggestUiState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = s.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}