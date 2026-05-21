package com.example.ass2.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2.data.local.Meal
import com.example.ass2.util.DateUtils
import com.example.ass2.viewmodel.MealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    navController: NavController,
    viewModel: MealViewModel,
    mealId: Int = -1,
    prefilledName: String = "",
    prefilledCalories: Int = -1,
    prefilledMealType: String = ""
){
    val isEditMode = mealId > 0

    var editingMeal by remember { mutableStateOf<Meal?>(null) }
    var isLoading by remember(mealId) { mutableStateOf(isEditMode) }
    var loadFailed by remember { mutableStateOf(false) }

    LaunchedEffect(mealId) {
        if (isEditMode) {
            isLoading = true
            loadFailed = false
            editingMeal = viewModel.getMealById(mealId)
            loadFailed = editingMeal == null
            isLoading = false
        } else {
            editingMeal = null
            loadFailed = false
        }
    }

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isEditMode && isLoading -> {
                Spacer(Modifier.height(48.dp))
                CircularProgressIndicator(color = green)
                Spacer(Modifier.height(12.dp))
                Text("Loading meal...", color = Color.Gray)
            }

            isEditMode && loadFailed -> {
                Spacer(Modifier.height(48.dp))
                Text("Meal not found.", color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    border = BorderStroke(1.dp, green)
                ) {
                    Text("Back", color = green)
                }
            }

            else -> {
                key(if (isEditMode) editingMeal?.id else -1) {
                    var name by remember(prefilledName, editingMeal?.id) {
                        mutableStateOf(
                            editingMeal?.name ?: prefilledName
                        )
                    }
                    var calories by remember(prefilledCalories, editingMeal?.id) {
                        mutableStateOf(
                            editingMeal?.calories?.toString()
                                ?: if (prefilledCalories >= 0) prefilledCalories.toString() else ""
                        )
                    }
                    var expanded by remember { mutableStateOf(false) }
                    var mealType by remember(prefilledMealType, editingMeal?.id) {
                        mutableStateOf(
                            editingMeal?.mealType
                                ?: prefilledMealType.ifBlank { "Breakfast" }
                        )
                    }

                    val options = listOf("Breakfast", "Lunch", "Dinner")

                    LaunchedEffect(editingMeal) {
                        val meal = editingMeal ?: return@LaunchedEffect
                        name = meal.name
                        calories = meal.calories.toString()
                        mealType = meal.mealType
                    }

                    val initialDateMillis =
                        editingMeal?.date ?: System.currentTimeMillis()

                    val dateState = rememberDatePickerState(
                        initialSelectedDateMillis = initialDateMillis
                    )
                    val selectedDateMillis =
                        dateState.selectedDateMillis ?: initialDateMillis

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            if (isEditMode) "Edit Meal" else "Add Meal",
                            style = MaterialTheme.typography.headlineMedium,
                            color = green,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(4.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Food Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = calories,
                            onValueChange = { calories = it },
                            label = { Text("Calories") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = mealType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Meal Type") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = lightGreen,
                                    unfocusedContainerColor = lightGreen,
                                    focusedIndicatorColor = green,
                                    unfocusedIndicatorColor = green,
                                    focusedLabelColor = green,
                                    unfocusedLabelColor = green
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = green) },
                                        onClick = {
                                            mealType = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Date: ${DateUtils.formatDate(selectedDateMillis)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = green,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(8.dp))

                        DatePicker(
                            state = dateState,
                            colors = DatePickerDefaults.colors(
                                containerColor = Color.White,
                                selectedDayContainerColor = green,
                                selectedDayContentColor = Color.White,
                                todayDateBorderColor = green,
                                todayContentColor = green
                            )
                        )

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val trimmedName = name.trim()
                                val cal = calories.toIntOrNull()
                                if (trimmedName.isBlank() || cal == null) return@Button

                                val date =
                                    dateState.selectedDateMillis ?: initialDateMillis

                                if (isEditMode) {
                                    val base = editingMeal ?: return@Button
                                    viewModel.updateMeal(
                                        base.copy(
                                            name = trimmedName,
                                            calories = cal,
                                            mealType = mealType,
                                            date = date
                                        )
                                    )
                                } else {
                                    viewModel.addMeal(
                                        name = trimmedName,
                                        calories = cal,
                                        mealType = mealType,
                                        date = date
                                    )
                                }

                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = green)
                        ) {
                            Text(if (isEditMode) "Update" else "Save")
                        }

                        Spacer(Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, green)
                        ) {
                            Text("Back", color = green)
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}