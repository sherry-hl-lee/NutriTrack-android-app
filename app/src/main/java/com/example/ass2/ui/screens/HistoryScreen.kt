package com.example.ass2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.ass2.util.DateUtils
import com.example.ass2.viewmodel.MealViewModel
@Composable
fun HistoryScreen(
    mealViewModel: MealViewModel
) {
    val meals by mealViewModel.meals.collectAsState(initial = emptyList())

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    var selectionMode by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Meal History",
                    style = MaterialTheme.typography.headlineMedium,
                    color = green
                )

                Row {
                    // 👉 未进入选择模式
                    if (!selectionMode) {
                        TextButton(onClick = {
                            selectionMode = true
                        }) {
                            Text("Select", color = green)
                        }
                    } else {
                        // 👉 Cancel（永远有）
                        TextButton(onClick = {
                            selectionMode = false
                            selectedItems = emptySet()
                        }) {
                            Text("Cancel", color = green)
                        }

                        // 👉 只有选中才显示 Delete
                        if (selectedItems.isNotEmpty()) {
                            TextButton(onClick = {
                                meals.filter { selectedItems.contains(it.id) }
                                    .forEach { mealViewModel.deleteMeal(it) }

                                selectedItems = emptySet()
                                selectionMode = false
                            }) {
                                Text("Delete (${selectedItems.size})", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(meals) { meal ->

                val isSelected = selectedItems.contains(meal.id)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            // ✅ 批量模式才显示 checkbox
                            if (selectionMode) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        selectedItems =
                                            if (it) selectedItems + meal.id
                                            else selectedItems - meal.id
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = green)
                                )
                            }

                            Column {
                                Text(meal.name, color = green, fontWeight = FontWeight.SemiBold)
                                Text("${meal.calories} kcal · ${meal.mealType}", color = Color.Gray)
                                Text(
                                    DateUtils.formatDateTime(meal.date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        if (!selectionMode) {
                            IconButton(
                                onClick = { mealViewModel.deleteMeal(meal) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}