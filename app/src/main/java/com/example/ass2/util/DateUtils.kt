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

    fun mealsOnDay(meals: List<Meal>, dayStart: Long): List<Meal> =
        meals.filter { startOfDay(it.date) == dayStart }.sortedByDescending { it.date }

    fun caloriesOnDay(meals: List<Meal>, dayStart: Long): Int =
        mealsOnDay(meals, dayStart).sumOf { it.calories }

    data class CalendarDayCell(
        val dayStart: Long?,
        val dayOfMonth: Int,
        val calories: Int,
        val isToday: Boolean,
        val isCurrentMonth: Boolean
    )

    fun buildMonthCalendar(
        meals: List<Meal>,
        year: Int,
        month: Int
    ): List<CalendarDayCell> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val firstWeekday = cal.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val todayStart = startOfDay(System.currentTimeMillis())

        val cells = mutableListOf<CalendarDayCell>()
        repeat(firstWeekday) {
            cells.add(CalendarDayCell(null, 0, 0, false, false))
        }
        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dayStart = cal.timeInMillis
            val cals = caloriesOnDay(meals, dayStart)
            cells.add(
                CalendarDayCell(
                    dayStart = dayStart,
                    dayOfMonth = day,
                    calories = cals,
                    isToday = dayStart == todayStart,
                    isCurrentMonth = true
                )
            )
        }
        return cells
    }

    fun formatMonthYear(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }

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
