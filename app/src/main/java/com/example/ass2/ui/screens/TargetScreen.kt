package com.example.ass2.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ass2.data.local.Target
import com.example.ass2.ui.components.TargetProgressRing
import com.example.ass2.viewmodel.TargetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TargetScreen(
    navController: NavController,
    targetViewModel: TargetViewModel
) {
    val green = Color(0xFF4CAF50)
    val greenDark = Color(0xFF2E7D32)
    val lightGreen = Color(0xFFE8F5E9)
    val accentOrange = Color(0xFFFFB74D)

    val targets = targetViewModel.targets
    val progress = if (targetViewModel.totalPoints == 0) 0f
    else targetViewModel.earnedPoints.toFloat() / targetViewModel.totalPoints
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "targetProgress"
    )
    val percent = (progress * 100).toInt()
    val completedCount = targets.count { it.completed }
    val todayLabel = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(Date())

    val motivation = when {
        progress >= 1f -> "Amazing — you crushed today's goals!"
        progress >= 0.66f -> "Almost there — finish strong!"
        progress > 0f -> "Nice momentum — keep going!"
        else -> "Small steps lead to big changes."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(green, greenDark)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    "Daily Target",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    todayLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TargetProgressRing(
                        progress = animatedProgress,
                        percentText = "$percent%",
                        pointsText = "${targetViewModel.earnedPoints}/${targetViewModel.totalPoints} pts",
                        progressColor = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        textColor = Color.White
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f).padding(start = 16.dp)
                    ) {
                        HeroStatChip("Completed", "$completedCount/${targets.size}", Color.White)
                        HeroStatChip(
                            "Remaining",
                            "${targetViewModel.totalPoints - targetViewModel.earnedPoints} pts",
                            accentOrange
                        )
                        Text(
                            motivation,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                SegmentedGoalBar(targets = targets)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Today's habits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = greenDark
                )
                Spacer(Modifier.height(4.dp))
            }

            items(targets, key = { it.id }) { target ->
                HabitTaskCard(
                    target = target,
                    green = green,
                    greenDark = greenDark,
                    onToggle = { targetViewModel.toggleTarget(target) }
                )
            }

            if (progress >= 1f) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            lightGreen,
                                            Color(0xFFC8E6C9)
                                        )
                                    )
                                )
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎉", fontSize = 36.sp)
                            Text(
                                "Goal Achieved!",
                                fontWeight = FontWeight.Bold,
                                color = greenDark,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "You earned all ${targetViewModel.totalPoints} points today.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun HeroStatChip(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SegmentedGoalBar(targets: List<Target>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        targets.forEach { target ->
            val segmentColor = if (target.completed) Color.White else Color.White.copy(alpha = 0.25f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(segmentColor)
            )
        }
    }
}

@Composable
private fun HabitTaskCard(
    target: Target,
    green: Color,
    greenDark: Color,
    onToggle: () -> Unit
) {
    val cardBg by animateColorAsState(
        targetValue = if (target.completed) Color(0xFFE8F5E9) else Color.White,
        animationSpec = tween(300),
        label = "cardBg"
    )
    val checkScale by animateFloatAsState(
        targetValue = if (target.completed) 1f else 0.85f,
        animationSpec = tween(300),
        label = "checkScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (target.completed) 2.dp else 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (target.completed) green else Color(0xFFBDBDBD))
            )

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (target.completed) green.copy(alpha = 0.15f) else lightGreenBox()),
                contentAlignment = Alignment.Center
            ) {
                Text(target.icon, fontSize = 24.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    target.title,
                    color = if (target.completed) greenDark else Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (target.completed) TextDecoration.LineThrough else null
                )
                Text(
                    target.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "+${target.points} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = green,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .scale(checkScale)
                    .clip(CircleShape)
                    .background(if (target.completed) green else Color(0xFFEEEEEE))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (target.completed) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun lightGreenBox(): Color = Color(0xFFF1F8E9)
