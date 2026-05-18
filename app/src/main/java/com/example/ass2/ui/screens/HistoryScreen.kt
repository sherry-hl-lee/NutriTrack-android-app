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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2.ui.components.MealMonthCalendar
import com.example.ass2.util.DateUtils
import com.example.ass2.viewmodel.MealViewModel
import java.util.Calendar

@Composable
fun HistoryScreen(
    navController: NavController,
    mealViewModel: MealViewModel
) {
    val meals by mealViewModel.meals.collectAsState(initial = emptyList())

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val now = remember { Calendar.getInstance() }
    var displayYear by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var displayMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH)) }

    val calendarCells = remember(meals, displayYear, displayMonth) {
        DateUtils.buildMonthCalendar(meals, displayYear, displayMonth)
    }
    val monthLabel = remember(displayYear, displayMonth) {
        DateUtils.formatMonthYear(displayYear, displayMonth)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Meal History",
            style = MaterialTheme.typography.headlineMedium,
            color = green,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            "Tap a date with kcal to view meals",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                MealMonthCalendar(
                    cells = calendarCells,
                    monthLabel = monthLabel,
                    green = green,
                    onPreviousMonth = {
                        if (displayMonth == Calendar.JANUARY) {
                            displayMonth = Calendar.DECEMBER
                            displayYear -= 1
                        } else {
                            displayMonth -= 1
                        }
                    },
                    onNextMonth = {
                        if (displayMonth == Calendar.DECEMBER) {
                            displayMonth = Calendar.JANUARY
                            displayYear += 1
                        } else {
                            displayMonth += 1
                        }
                    },
                    onDayClick = { dayStart ->
                        navController.navigate("meals_day/$dayStart")
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
