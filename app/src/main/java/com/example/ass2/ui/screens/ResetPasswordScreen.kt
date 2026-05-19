package com.example.ass2.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2.viewmodel.UserViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                "Reset Password",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = green
            )

            Text(
                "Verify your email, then set a new password",
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        enabled = !isVerified && !isVerifying,
                        isError = emailError != null,
                        supportingText = emailError?.let { message ->
                            {
                                Text(
                                    text = message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                emailError = "Please enter your email"
                                return@Button
                            }
                            isVerifying = true
                            userViewModel.verifyEmailExists(email) { exists ->
                                isVerifying = false
                                if (exists) {
                                    emailError = null
                                    isVerified = true
                                    Toast.makeText(
                                        context,
                                        "Email verified successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    isVerified = false
                                    emailError = "No account found for this email"
                                    Toast.makeText(
                                        context,
                                        "Email not registered",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        enabled = !isVerified && !isVerifying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = green)
                    ) {
                        Text(if (isVerifying) "Verifying..." else "Verify")
                    }

                    if (isVerified) {
                        Spacer(Modifier.height(20.dp))

                        ResetPasswordField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "New password",
                            visible = newPasswordVisible,
                            onVisibilityToggle = { newPasswordVisible = !newPasswordVisible }
                        )

                        Spacer(Modifier.height(20.dp))

                        ResetPasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm new password",
                            visible = confirmPasswordVisible,
                            onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                        )

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                when {
                                    newPassword.isBlank() || confirmPassword.isBlank() -> {
                                        Toast.makeText(
                                            context,
                                            "Please fill in both password fields",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    newPassword != confirmPassword -> {
                                        Toast.makeText(
                                            context,
                                            "Passwords do not match",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else -> {
                                        userViewModel.resetPassword(email, newPassword) { result ->
                                            when (result) {
                                                is UserViewModel.ResetPasswordResult.Success -> {
                                                    Toast.makeText(
                                                        context,
                                                        "Password updated successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.navigate("login") {
                                                        popUpTo("reset_password") { inclusive = true }
                                                    }
                                                }
                                                is UserViewModel.ResetPasswordResult.UserNotFound -> {
                                                    Toast.makeText(
                                                        context,
                                                        "No account found for this email",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = green)
                        ) {
                            Text("Confirm")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("reset_password") { inclusive = true }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Back to login", color = green)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResetPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onVisibilityToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    imageVector = if (visible) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    },
                    contentDescription = if (visible) "Hide password" else "Show password"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
