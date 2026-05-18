package com.example.ass2.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PieSlice(
    val label: String,
    val value: Int,
    val color: Color
)

@Composable
fun CaloriesPieChart(
    slices: List<PieSlice>,
    centerTitle: String,
    centerSubtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val total = slices.sumOf { it.value }
    val chartSize = 180.dp
    val centerHoleSize = 108.dp
    val ringColor = Color(0xFFE8E8E8)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(chartSize)
        ) {
            Canvas(modifier = Modifier.size(chartSize)) {
                val strokeWidth = size.minDimension * 0.22f
                val arcSize = Size(size.width, size.height)
                val topLeft = Offset.Zero
                val arcStyle = Stroke(width = strokeWidth, cap = StrokeCap.Butt)

                if (total > 0) {
                    var startAngle = -90f
                    slices.forEach { slice ->
                        val sweep = 360f * slice.value / total
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = arcStyle
                        )
                        startAngle += sweep
                    }
                } else {
                    drawArc(
                        color = ringColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = arcStyle
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(centerHoleSize)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = centerTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Text(
                        text = centerSubtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }

        if (slices.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                slices.forEach { slice ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(slice.color, CircleShape)
                        )
                        Text(
                            "${slice.label} ${slice.value}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        Text(
            "Tap chart to view today's meals",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF66BB6A),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
