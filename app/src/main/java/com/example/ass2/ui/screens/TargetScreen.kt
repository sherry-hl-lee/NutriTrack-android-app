package com.example.ass2.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ass2.viewmodel.TargetViewModel

@Composable
fun TargetScreen(
    navController: NavController,
    targetViewModel: TargetViewModel = viewModel()
) {

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val targets = targetViewModel.targets

    val progress =
        if (targetViewModel.totalPoints == 0) 0f
        else targetViewModel.earnedPoints.toFloat() / targetViewModel.totalPoints

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(16.dp)
    ) {

        Text(
            "Daily Target",
            style = MaterialTheme.typography.headlineMedium,
            color = green
        )

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(contentAlignment = Alignment.Center) {

                    CircularProgressIndicator(
                        progress = progress,
                        color = green,
                        strokeWidth = 10.dp,
                        modifier = Modifier.size(100.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${(progress * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            color = green
                        )
                        Text(
                            "${targetViewModel.earnedPoints}/${targetViewModel.totalPoints}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(targets) { target ->

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text(target.title, color = green)
                            Text("${target.points} pts", color = Color.Gray)
                        }

                        Checkbox(
                            checked = target.completed,
                            onCheckedChange = {
                                targetViewModel.toggleTarget(target)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = green)
                        )
                    }
                }
            }
        }

        if (progress == 1f) {
            Text(
                text = "🎉 Goal Achieved!",
                color = green,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}