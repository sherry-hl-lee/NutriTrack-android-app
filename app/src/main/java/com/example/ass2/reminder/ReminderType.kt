package com.example.ass2.reminder

enum class ReminderType(
    val id: String,
    val label: String,
    val notificationTitle: String,
    val notificationBody: String,
    val dialogTitle: String,
    val dialogBody: String
) {
    WATER(
        id = "water",
        label = "Drink water",
        notificationTitle = "Hydration reminder",
        notificationBody = "Time for a glass of water — stay hydrated.",
        dialogTitle = "Time to drink water",
        dialogBody = "Take a moment to log water or a drink in NutriTrack."
    ),
    BREAKFAST(
        id = "breakfast",
        label = "Breakfast",
        notificationTitle = "Breakfast reminder",
        notificationBody = "Log your breakfast and check today's calories.",
        dialogTitle = "Time for breakfast",
        dialogBody = "Open NutriTrack to log your breakfast."
    ),
    LUNCH(
        id = "lunch",
        label = "Lunch",
        notificationTitle = "Lunch reminder",
        notificationBody = "Log your lunch and keep your day on track.",
        dialogTitle = "Time for lunch",
        dialogBody = "Open NutriTrack to log your lunch."
    ),
    DINNER(
        id = "dinner",
        label = "Dinner",
        notificationTitle = "Dinner reminder",
        notificationBody = "Log your dinner and review your nutrition today.",
        dialogTitle = "Time for dinner",
        dialogBody = "Open NutriTrack to log your dinner."
    );

    companion object {
        fun fromId(id: String): ReminderType? = entries.firstOrNull { it.id == id }
    }
}
