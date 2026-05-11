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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.ass2.viewmodel.MealViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    navController: NavController,
    viewModel: MealViewModel
) {

    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var mealType by remember { mutableStateOf("Breakfast") }

    val options = listOf("Breakfast", "Lunch", "Dinner")

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val dateState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .verticalScroll(rememberScrollState()) // ✅ 防止挤爆屏幕
            .padding(12.dp)
    ) {

        Text(
            "Add Meal",
            style = MaterialTheme.typography.headlineMedium,
            color = green
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
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
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
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it, color = green) },
                        onClick = {
                            mealType = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

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
                if (name.isNotBlank() && calories.isNotBlank()) {

                    val selectedDate =
                        dateState.selectedDateMillis ?: System.currentTimeMillis()

                    viewModel.addMeal(
                        name = name,
                        calories = calories.toIntOrNull() ?: 0,
                        mealType = mealType,
                        date = dateState.selectedDateMillis ?: System.currentTimeMillis() // ✅ 关键修复
                    )

                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green)
        ) {
            Text("Save")
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