package com.example.ass2.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.ass2.data.PresetFoodCatalog
import com.example.ass2.util.DateUtils
import com.example.ass2.viewmodel.MealViewModel
import com.example.ass2.viewmodel.UserViewModel

enum class FoodSearchSource { Preset, MyFood }

data class FoodSearchItem(
    val name: String,
    val calories: Int,
    val mealType: String,
    val source: FoodSearchSource
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    mealViewModel: MealViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val userMeals by mealViewModel.meals.collectAsState(initial = emptyList())
    val currentUser by userViewModel.currentUser.collectAsState()
    val isGuest by userViewModel.isGuest.collectAsState()
    val isLoggedIn = currentUser != null && !isGuest

    var query by remember { mutableStateOf("") }
    var breakfastChecked by remember { mutableStateOf(false) }
    var lunchChecked by remember { mutableStateOf(false) }
    var dinnerChecked by remember { mutableStateOf(false) }
    var lowCalorieOnly by remember { mutableStateOf(false) }
    var highCalorieOnly by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }
    var itemToAdd by remember { mutableStateOf<FoodSearchItem?>(null) }

    val addDateState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val selectedTypes = buildList {
        if (breakfastChecked) add("Breakfast")
        if (lunchChecked) add("Lunch")
        if (dinnerChecked) add("Dinner")
    }

    val allItems = remember(userMeals, isLoggedIn) {
        val presets = PresetFoodCatalog.foods.map { food ->
            FoodSearchItem(
                name = food.name,
                calories = food.calories,
                mealType = food.mealType,
                source = FoodSearchSource.Preset
            )
        }
        val myFoods = if (isLoggedIn) {
            userMeals.map { meal ->
                FoodSearchItem(
                    name = meal.name,
                    calories = meal.calories,
                    mealType = meal.mealType,
                    source = FoodSearchSource.MyFood
                )
            }
        } else {
            emptyList()
        }
        presets + myFoods
    }

    val filteredItems = allItems.filter { item ->
        val matchQuery = query.isBlank() ||
            item.name.contains(query, ignoreCase = true)

        val matchType =
            selectedTypes.isEmpty() || selectedTypes.contains(item.mealType)

        val matchCalories = when {
            lowCalorieOnly -> item.calories < 500
            highCalorieOnly -> item.calories >= 500
            else -> true
        }

        matchQuery && matchType && matchCalories
    }

    fun openAddDialog(item: FoodSearchItem) {
        if (!isLoggedIn) {
            showLoginRequiredDialog = true
            return
        }
        itemToAdd = item
        showAddDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(16.dp)
    ) {

        Text(
            "Search Food",
            style = MaterialTheme.typography.headlineMedium,
            color = green
        )

        Spacer(Modifier.height(8.dp))

        Text(
            if (isLoggedIn) {
                "Search preset foods or your meals · Tap Add Meal to save with a date"
            } else {
                "Preset foods for everyone · Log in to add meals"
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search food") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {

                Text("Filter", fontWeight = FontWeight.Bold, color = green)

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Low calorie (<500 kcal)")
                    Switch(
                        checked = lowCalorieOnly,
                        onCheckedChange = {
                            lowCalorieOnly = it
                            if (it) highCalorieOnly = false
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = green)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("High calorie (≥500 kcal)")
                    Switch(
                        checked = highCalorieOnly,
                        onCheckedChange = {
                            highCalorieOnly = it
                            if (it) lowCalorieOnly = false
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = green)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Checkbox(
                        checked = breakfastChecked,
                        onCheckedChange = { breakfastChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = green)
                    )
                    Text("Breakfast")

                    Spacer(Modifier.width(12.dp))

                    Checkbox(
                        checked = lunchChecked,
                        onCheckedChange = { lunchChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = green)
                    )
                    Text("Lunch")

                    Spacer(Modifier.width(12.dp))

                    Checkbox(
                        checked = dinnerChecked,
                        onCheckedChange = { dinnerChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = green)
                    )
                    Text("Dinner")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Results: ${filteredItems.size}",
            color = Color.Gray
        )

        Spacer(Modifier.height(8.dp))

        if (filteredItems.isEmpty()) {
            Text(
                "No results found",
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(filteredItems) { item ->

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        item.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = green,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "${item.calories} kcal · ${item.mealType}",
                                        color = Color.Gray
                                    )
                                    Text(
                                        when (item.source) {
                                            FoodSearchSource.Preset -> "Preset"
                                            FoodSearchSource.MyFood -> "My food"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (item.source == FoodSearchSource.Preset) {
                                            Color(0xFF2E7D32)
                                        } else {
                                            Color(0xFF1565C0)
                                        }
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = { openAddDialog(item) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = green)
                            ) {
                                Text(
                                    if (isLoggedIn) "Add Meal" else "Log in to add",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, green)
        ) {
            Text("Back", color = green)
        }
    }

    if (showAddDialog && itemToAdd != null) {
        val item = itemToAdd!!
        val selectedDate = addDateState.selectedDateMillis ?: System.currentTimeMillis()

        Dialog(
            onDismissRequest = {
                showAddDialog = false
                itemToAdd = null
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Add Meal",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = green
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        item.name,
                        fontWeight = FontWeight.Bold,
                        color = green,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "${item.calories} kcal · ${item.mealType}",
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Text(
                        "Select date",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = green
                    )
                    Text(
                        DateUtils.formatDate(selectedDate),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    DatePicker(
                        state = addDateState,
                        modifier = Modifier.fillMaxWidth(),
                        colors = DatePickerDefaults.colors(
                            containerColor = Color.White,
                            selectedDayContainerColor = green,
                            selectedDayContentColor = Color.White,
                            todayDateBorderColor = green,
                            todayContentColor = green
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            showAddDialog = false
                            itemToAdd = null
                        }) {
                            Text("Cancel", color = Color.Gray)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                mealViewModel.addMeal(
                                    name = item.name,
                                    calories = item.calories,
                                    mealType = item.mealType,
                                    date = selectedDate
                                )
                                Toast.makeText(
                                    context,
                                    "${item.name} added for ${DateUtils.formatDate(selectedDate)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showAddDialog = false
                                itemToAdd = null
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = green)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }

    if (showLoginRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showLoginRequiredDialog = false },
            title = { Text("Login required") },
            text = { Text("Please register or log in to add meals.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLoginRequiredDialog = false
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Log in", color = green)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginRequiredDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
