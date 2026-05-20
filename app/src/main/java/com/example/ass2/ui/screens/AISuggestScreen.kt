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
import com.example.ass2.viewmodel.MealSuggestSource
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
            "AI meal ideas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = green
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Suggestions use your profile, calorie goal, and today’s logged meals.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    "Setup (optional)",
                    fontWeight = FontWeight.SemiBold,
                    color = green
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "For real AI text, create a key in Google AI Studio and add this line to local.properties:\n\n" +
                            "GEMINI_API_KEY=your_key_here\n\n" +
                            "Without a key, the app still shows offline ideas from your data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }

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
            Text("Get suggestions")
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
                    "Tap “Get suggestions” to generate ideas.",
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
                val label = when (s.source) {
                    MealSuggestSource.Gemini -> "Gemini"
                    MealSuggestSource.Offline -> "Offline"
                }
                Text(
                    "Source: $label",
                    style = MaterialTheme.typography.labelLarge,
                    color = green,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    s.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1B5E20)
                )
            }
        }
    }
}