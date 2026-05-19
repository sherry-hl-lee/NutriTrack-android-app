package com.example.ass2.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
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
fun SignupScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var genderExpanded by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var showRequiredErrors by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)
    val genderOptions = listOf("Male", "Female", "Other")
    val scrollState = rememberScrollState()

    val weightError = validateWeight(weight, required = showRequiredErrors)
    val heightError = validateHeight(height, required = showRequiredErrors)
    val ageError = validateAge(age, required = showRequiredErrors)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = green
            )

            Text(
                "Join NutriTrack and start your journey",
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

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

                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        gender = option
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please enter email and password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val w = parseValidatedWeight(weight)
                            val h = parseValidatedHeight(height)
                            val a = parseValidatedAge(age)

                            if (w == null || h == null || a == null) {
                                showRequiredErrors = true
                                Toast.makeText(
                                    context,
                                    "Please fix weight, height, and age before signing up",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            userViewModel.signup(
                                email = email,
                                password = password,
                                weight = w,
                                height = h,
                                age = a,
                                gender = gender
                            ) { success ->
                                dialogMessage = if (success) {
                                    "Signup successful!"
                                } else {
                                    "Email already exists, please try again"
                                }
                                showDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = green)
                    ) {
                        Text("Sign Up")
                    }

                    Spacer(Modifier.height(16.dp))

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
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Result") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        if (dialogMessage == "Signup successful!") {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
