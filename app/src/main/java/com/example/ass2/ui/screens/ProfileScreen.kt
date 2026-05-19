package com.example.ass2.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ass2.data.local.User
import com.example.ass2.ui.components.ProfileMetricTextField
import com.example.ass2.util.parseValidatedAge
import com.example.ass2.util.parseValidatedHeight
import com.example.ass2.util.parseValidatedWeight
import com.example.ass2.util.validateAge
import com.example.ass2.util.validateHeight
import com.example.ass2.util.validateWeight
import com.example.ass2.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {

    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showRequiredErrors by remember { mutableStateOf(false) }

    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var gender by remember { mutableStateOf("Male") }
    var expanded by remember { mutableStateOf(false) }

    val options = listOf("Male", "Female", "Other")

    fun applyUserToForm(user: User?) {
        if (user == null) {
            weight = ""
            height = ""
            age = ""
            gender = "Male"
        } else {
            weight = if (user.weight > 0f) user.weight.toString() else ""
            height = if (user.height > 0f) user.height.toString() else ""
            age = if (user.age > 0) user.age.toString() else ""
            gender = user.gender.ifBlank { "Male" }
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser?.email != null) {
            userViewModel.refreshCurrentUserFromDb()
        }
        applyUserToForm(userViewModel.currentUser.value)
    }

    val weightError = validateWeight(weight, required = showRequiredErrors)
    val heightError = validateHeight(height, required = showRequiredErrors)
    val ageError = validateAge(age, required = showRequiredErrors)

    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
            .padding(16.dp)
    ) {

        Text(
            "Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = green
        )

        Spacer(Modifier.height(16.dp))

        ProfileMetricTextField(
            value = weight,
            onValueChange = { weight = it },
            label = "Weight (kg)",
            errorMessage = weightError
        )

        Spacer(Modifier.height(12.dp))

        ProfileMetricTextField(
            value = height,
            onValueChange = { height = it },
            label = "Height (cm)",
            errorMessage = heightError
        )

        Spacer(Modifier.height(12.dp))

        ProfileMetricTextField(
            value = age,
            onValueChange = { age = it },
            label = "Age",
            errorMessage = ageError
        )

        Spacer(Modifier.height(12.dp))

        //  Gender Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Gender") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            gender = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        //  Save Button
        Button(
            onClick = {
                if (currentUser == null) {
                    Toast.makeText(context, "Please log in to save profile", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val w = parseValidatedWeight(weight)
                val h = parseValidatedHeight(height)
                val a = parseValidatedAge(age)

                if (w != null && h != null && a != null) {
                    userViewModel.updateUserProfile(
                        weight = w,
                        height = h,
                        age = a,
                        gender = gender
                    ) {
                        Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("profile_summary")
                    }
                } else {
                    showRequiredErrors = true
                    Toast.makeText(context, "Please fix weight, height, and age", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green)
        ) {
            Text("Save")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Text("Log out")
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.logout()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        showLogoutDialog = false
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Confirm", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}