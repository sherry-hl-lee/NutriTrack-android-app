package com.example.ass2.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ass2.viewmodel.UserViewModel

@Composable
fun MainLayout(
    navController: NavController,
    userViewModel: UserViewModel,
    content: @Composable () -> Unit
) {
    val green = Color(0xFF4CAF50)
    val lightGreen = Color(0xFFE8F5E9)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isProfileSection =
        currentRoute == "profile" || currentRoute == "profile_summary"

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {

                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = if (currentRoute == "home") green else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            "Home",
                            color = if (currentRoute == "home") green else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = lightGreen,
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "target",
                    onClick = {
                        navController.navigate("target") {
                            popUpTo("home")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (currentRoute == "target") green else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            "Target",
                            color = if (currentRoute == "target") green else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = lightGreen
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = {
                        navController.navigate("history") {
                            popUpTo("home")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            tint = if (currentRoute == "history") green else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            "History",
                            color = if (currentRoute == "history") green else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = lightGreen,
                    )
                )

                NavigationBarItem(
                    selected = isProfileSection,
                    onClick = {
                        val route =
                            if (userViewModel.hasSavedProfile()) "profile_summary" else "profile"
                        navController.navigate(route) {
                            popUpTo("home")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isProfileSection) green else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            "Profile",
                            color = if (isProfileSection) green else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = lightGreen,
                    )
                )
            }
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(lightGreen)
        ) {
            content()
        }
    }
}