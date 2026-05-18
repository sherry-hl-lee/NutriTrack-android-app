package com.example.ass2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.util.DateUtils

@Composable
fun MealMonthCalendar(
    cells: List<DateUtils.CalendarDayCell>,
    monthLabel: String,
    green: Color,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (Long) -> Unit
) {
    val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous month", tint = green)
            }
            Text(monthLabel, fontWeight = FontWeight.Bold, color = green)
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next month", tint = green)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        val rows = cells.chunked(7)
        rows.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { cell ->
                    CalendarDayCellView(
                        cell = cell,
                        green = green,
                        modifier = Modifier.weight(1f),
                        onClick = { dayStart -> onDayClick(dayStart) }
                    )
                }
                repeat(7 - week.size) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCellView(
    cell: DateUtils.CalendarDayCell,
    green: Color,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit
) {
    if (cell.dayStart == null) {
        Box(modifier = modifier.aspectRatio(1f))
        return
    }

    val hasMeals = cell.calories > 0
    val bg = when {
        cell.isToday -> Color(0xFFE8F5E9)
        hasMeals -> Color(0xFFF1F8E9)
        else -> Color.White
    }
    val borderColor = if (cell.isToday) green else Color(0xFFE0E0E0)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(bg, RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = hasMeals) { onClick(cell.dayStart!!) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = cell.dayOfMonth.toString(),
                fontWeight = if (cell.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (cell.isToday) green else Color.DarkGray,
                fontSize = 13.sp
            )
            if (hasMeals) {
                Text(
                    text = "${cell.calories}",
                    style = MaterialTheme.typography.labelSmall,
                    color = green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 8.sp
                )
            }
        }
    }
}
