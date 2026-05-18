package com.example.ass2.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ass2.util.DateUtils

@Composable
fun CaloriesBarChart(
    dailyData: List<DateUtils.DayCalories>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF4CAF50)
) {
    val maxCalories = dailyData.maxOfOrNull { it.calories }?.coerceAtLeast(1) ?: 1

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 4.dp)
        ) {
            val barCount = dailyData.size.coerceAtLeast(1)
            val gap = size.width * 0.04f
            val barWidth = (size.width - gap * (barCount + 1)) / barCount
            val chartBottom = size.height * 0.88f
            val chartTop = size.height * 0.08f
            val chartHeight = chartBottom - chartTop

            dailyData.forEachIndexed { index, day ->
                val barHeight = (day.calories.toFloat() / maxCalories) * chartHeight
                val left = gap + index * (barWidth + gap)
                val top = chartBottom - barHeight

                drawRoundRect(
                    color = if (day.calories > 0) barColor else Color(0xFFE0E0E0),
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                if (day.calories > 0) {
                    // value on top of bar
                }
            }
        }

        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            dailyData.forEach { day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = if (day.calories > 0) "${day.calories}" else "-",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = barColor
                    )
                }
            }
        }
    }
}
