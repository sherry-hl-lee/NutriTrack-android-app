package com.example.ass2.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2.ui.components.CaloriesBarChart
import com.example.ass2.ui.components.DailyTargetAchievementChart
import com.example.ass2.util.DateUtils
import com.example.ass2.viewmodel.MealViewModel
import com.example.ass2.viewmodel.TargetViewModel

@Composable
fun InsightsScreen(
    navController: NavController,
    mealViewModel: MealViewModel,
    targetViewModel: TargetViewModel
) {
    val meals by mealViewModel.meals.collectAsState(initial = emptyList())
    val targetLogs by targetViewModel.recentLogs.collectAsState()
    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)
    val greenDark = Color(0xFF2E7D32)

    val dailyData = remember(meals) { DateUtils.caloriesPerDay(meals, 7) }
    val targetDailyStatus = remember(targetLogs) { DateUtils.targetStatusPerDay(targetLogs, 7) }
    val achievedDays = targetDailyStatus.count { it.achieved }
    val todayTarget = targetDailyStatus.lastOrNull()
    val mealTypeTotals = remember(meals) { DateUtils.caloriesByMealType(meals) }
    val totalMeals = meals.size
    val totalCalories = meals.sumOf { it.calories }
    val avgPerDay = if (dailyData.isNotEmpty()) totalCalories / dailyData.size else 0
    val maxDay = dailyData.maxByOrNull { it.calories }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Insights",
            style = MaterialTheme.typography.headlineMedium,
            color = green,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Meals & daily habits over the last 7 days",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Daily target achievements",
                    fontWeight = FontWeight.Bold,
                    color = green
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$achievedDays / 7 days goal completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(12.dp))
                DailyTargetAchievementChart(
                    dailyStatus = targetDailyStatus,
                    achievedColor = green
                )
                todayTarget?.let { today ->
                    Spacer(Modifier.height(12.dp))
                    if (today.achieved) {
                        Text(
                            "🎉 Today's target completed! (${today.earnedPoints}/${today.totalPoints} pts)",
                            color = greenDark,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (today.earnedPoints > 0) {
                        Text(
                            "Today: ${today.earnedPoints}/${today.totalPoints} pts — keep going!",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            "Today: complete habits on the Target tab to earn points.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (meals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    "No meal records yet. Add meals with dates to see charts here.",
                    modifier = Modifier.padding(20.dp),
                    color = Color.Gray
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("Meals", "$totalMeals", Modifier.weight(1f), green)
                StatCard("Avg / day", "$avgPerDay kcal", Modifier.weight(1f), green)
            }

            Spacer(Modifier.height(8.dp))

            maxDay?.let { day ->
                if (day.calories > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            "Highest day: ${day.label} · ${day.calories} kcal",
                            modifier = Modifier.padding(12.dp),
                            color = green,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Daily calories (7 days)",
                        fontWeight = FontWeight.Bold,
                        color = green
                    )
                    Spacer(Modifier.height(12.dp))
                    CaloriesBarChart(dailyData = dailyData, barColor = green)
                }
            }

            Spacer(Modifier.height(12.dp))

            if (mealTypeTotals.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Calories by meal type",
                            fontWeight = FontWeight.Bold,
                            color = green,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        val maxTypeCal = mealTypeTotals.values.maxOrNull()?.coerceAtLeast(1) ?: 1
                        mealTypeTotals.forEach { (type, cals) ->
                            Text(type, color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = cals.toFloat() / maxTypeCal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp),
                                color = green,
                                trackColor = lightGreen
                            )
                            Text(
                                "$cals kcal",
                                color = green,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Daily breakdown",
                        fontWeight = FontWeight.Bold,
                        color = green,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    dailyData.filter { it.calories > 0 }.reversed().forEach { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(DateUtils.formatDayLabel(day.dayStart), color = Color.DarkGray)
                            Text("${day.calories} kcal", color = green, fontWeight = FontWeight.SemiBold)
                        }
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
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, green)
        ) {
            Text("Back", color = green)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    green: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Bold, color = green)
        }
    }
}
