package com.example.ass2.data

/** Built-in foods/drinks — visible to all users and guests in Search Food. */
data class PresetFood(
    val name: String,
    val calories: Int,
    val mealType: String
)

object PresetFoodCatalog {
    val foods: List<PresetFood> = listOf(
        PresetFood("Apple", 95, "Breakfast"),
        PresetFood("Banana", 105, "Breakfast"),
        PresetFood("Scrambled eggs (2)", 180, "Breakfast"),
        PresetFood("Oatmeal (1 bowl)", 150, "Breakfast"),
        PresetFood("Whole milk (250ml)", 150, "Breakfast"),
        PresetFood("Greek yogurt", 120, "Breakfast"),
        PresetFood("Toast (2 slices)", 160, "Breakfast"),
        PresetFood("Coffee (black)", 5, "Breakfast"),
        PresetFood("Chicken breast (100g)", 165, "Lunch"),
        PresetFood("Brown rice (1 cup)", 215, "Lunch"),
        PresetFood("Green salad", 80, "Lunch"),
        PresetFood("Turkey sandwich", 320, "Lunch"),
        PresetFood("Orange juice (250ml)", 110, "Lunch"),
        PresetFood("Green tea", 2, "Lunch"),
        PresetFood("Milk tea", 350, "Lunch"),
        PresetFood("Grilled salmon (150g)", 280, "Dinner"),
        PresetFood("Spaghetti pasta", 350, "Dinner"),
        PresetFood("Beef steak (100g)", 250, "Dinner"),
        PresetFood("Pizza slice", 285, "Dinner"),
        PresetFood("Broccoli (1 cup)", 55, "Dinner"),
        // High calorie options
        PresetFood("Cheeseburger", 650, "Lunch"),
        PresetFood("Bacon cheeseburger", 780, "Lunch"),
        PresetFood("French fries (large)", 520, "Lunch"),
        PresetFood("Fried chicken (3 pcs)", 580, "Dinner"),
        PresetFood("Mac and cheese", 610, "Dinner"),
        PresetFood("Pepperoni pizza (2 slices)", 570, "Dinner"),
        PresetFood("Fried rice (large)", 550, "Lunch"),
        PresetFood("Ramen bowl", 600, "Dinner"),
        PresetFood("Nachos with cheese", 720, "Lunch"),
        PresetFood("Chocolate cake slice", 550, "Dinner"),
        PresetFood("Chocolate shake", 580, "Lunch"),
        PresetFood("Bubble milk tea (large)", 520, "Lunch"),
        PresetFood("Ice cream sundae", 540, "Dinner")
    )
}
