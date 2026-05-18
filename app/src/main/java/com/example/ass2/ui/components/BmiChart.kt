package com.example.ass2.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

private const val BMI_MIN = 15f
private const val BMI_MAX = 40f

private data class BmiSegment(
    val from: Float,
    val to: Float,
    val color: Color,
    val label: String
)

private val segments = listOf(
    BmiSegment(15f, 18.5f, Color(0xFF42A5F5), "Under"),
    BmiSegment(18.5f, 25f, Color(0xFF4CAF50), "Normal"),
    BmiSegment(25f, 30f, Color(0xFFFFA726), "Over"),
    BmiSegment(30f, 40f, Color(0xFFE53935), "Obese")
)

fun bmiStatusColor(bmi: Float): Color =
    segments.firstOrNull { bmi < it.to }?.color ?: segments.last().color

@Composable
fun BmiChart(
    bmi: Float,
    weightStatus: String,
    modifier: Modifier = Modifier
) {
    val markerColor = bmiStatusColor(bmi)
    val fraction = ((bmi - BMI_MIN) / (BMI_MAX - BMI_MIN)).coerceIn(0f, 1f)
    val barHeight = 24.dp
    val markerWidth = 16.dp
    val markerTotalHeight = 20.dp

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = String.format(Locale.US, "%.1f", bmi),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = markerColor
            )
            Text(
                text = "  BMI",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = weightStatus,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = markerColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, bottom = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight + markerTotalHeight)
        ) {
            val density = LocalDensity.current
            val markerOffsetX = with(density) {
                (maxWidth * fraction - markerWidth / 2).coerceIn(0.dp, maxWidth - markerWidth)
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .align(Alignment.BottomStart)
            ) {
                val totalWidth = size.width
                val h = size.height
                segments.forEach { segment ->
                    val startFrac = ((segment.from - BMI_MIN) / (BMI_MAX - BMI_MIN)).coerceIn(0f, 1f)
                    val endFrac = ((segment.to - BMI_MIN) / (BMI_MAX - BMI_MIN)).coerceIn(0f, 1f)
                    val left = startFrac * totalWidth
                    val width = (endFrac - startFrac) * totalWidth
                    if (width > 0f) {
                        drawRoundRect(
                            color = segment.color,
                            topLeft = Offset(left, 0f),
                            size = Size(width, h),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .size(markerWidth, markerTotalHeight)
                    .align(Alignment.BottomStart)
                    .offset(x = markerOffsetX, y = -barHeight)
            ) {
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(w / 2f, h)
                    lineTo(0f, 0f)
                    lineTo(w, 0f)
                    close()
                }
                drawPath(path, color = markerColor)
                drawPath(path, color = Color.White, style = Stroke(width = 2f))
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("15", "18.5", "25", "30", "40").forEach { label ->
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            segments.forEach { segment ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(segment.color, CircleShape)
                    )
                    Text(
                        text = segment.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
