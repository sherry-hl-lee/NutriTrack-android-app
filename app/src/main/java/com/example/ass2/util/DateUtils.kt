package com.example.ass2.util

import com.example.ass2.data.local.DailyTargetLog
import com.example.ass2.data.local.Meal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun isSameDay(first: Long, second: Long): Boolean =
        startOfDay(first) == startOfDay(second)

    fun isToday(millis: Long): Boolean =
        isSameDay(millis, System.currentTimeMillis())

    fun formatDate(millis: Long): String =
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))

    fun formatDateTime(millis: Long): String =
        SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(Date(millis))

    fun formatDayLabel(millis: Long): String {
        val today = startOfDay(System.currentTimeMillis())
        val day = startOfDay(millis)
        if (day == today) return "Today"
        val cal = Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, -1)
        }
        if (day == cal.timeInMillis) return "Yesterday"
        return SimpleDateFormat("EEE MMM dd", Locale.getDefault()).format(Date(millis))
    }

    fun formatChartLabel(millis: Long): String =
        SimpleDateFormat("EEE", Locale.getDefault()).format(Date(millis))

    data class DayCalories(val dayStart: Long, val label: String, val calories: Int)

    fun caloriesPerDay(meals: List<Meal>, dayCount: Int = 7): List<DayCalories> {
        val cal = Calendar.getInstance()
        return (dayCount - 1 downTo 0).map { daysAgo ->
            cal.timeInMillis = System.currentTimeMillis()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dayStart = cal.timeInMillis
            val total = meals
                .filter { startOfDay(it.date) == dayStart }
                .sumOf { it.calories }
            DayCalories(
                dayStart = dayStart,
                label = formatChartLabel(dayStart),
                calories = total
            )
        }
    }

    fun caloriesByMealType(meals: List<Meal>): Map<String, Int> =
        meals.groupBy { it.mealType }.mapValues { (_, list) -> list.sumOf { it.calories } }

    data class DayTargetStatus(
        val dayStart: Long,
        val label: String,
        val achieved: Boolean,
        val earnedPoints: Int,
        val totalPoints: Int
    )

    fun targetStatusPerDay(logs: List<DailyTargetLog>, dayCount: Int = 7): List<DayTargetStatus> {
        val logByDay = logs.associateBy { it.dayStart }
        val cal = Calendar.getInstance()
        return (dayCount - 1 downTo 0).map { daysAgo ->
            cal.timeInMillis = System.currentTimeMillis()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dayStart = cal.timeInMillis
            val log = logByDay[dayStart]
            DayTargetStatus(
                dayStart = dayStart,
                label = formatChartLabel(dayStart),
                achieved = log?.isGoalAchieved == true,
                earnedPoints = log?.earnedPoints ?: 0,
                totalPoints = log?.totalPoints ?: 0
            )
        }
    }
}
