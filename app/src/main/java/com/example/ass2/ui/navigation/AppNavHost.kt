package com.example.ass2.ui.navigation

import UserRepository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.repository.MealRepository
import com.example.ass2.reminder.ReminderPreferences
import com.example.ass2.ui.components.MealReminderAlertDialog
import com.example.ass2.ui.screens.AddMealScreen
import com.example.ass2.ui.screens.HistoryScreen
import com.example.ass2.ui.screens.HomeScreen
import com.example.ass2.ui.screens.LoginScreen
import com.example.ass2.ui.screens.ProfileScreen
import com.example.ass2.ui.screens.ProfileSummaryScreen
import com.example.ass2.ui.screens.ReminderScreen
import com.example.ass2.ui.screens.SearchScreen
import com.example.ass2.ui.screens.SignupScreen
import com.example.ass2.ui.screens.TargetScreen
import com.example.ass2.ui.screens.UserListScreen
import com.example.ass2.viewmodel.MealViewModel
import com.example.ass2.viewmodel.UserViewModel

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showMealAlert by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (ReminderPreferences(context).consumePendingMealAlert()) {
                    showMealAlert = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val db = AppDatabase.getDatabase(context)
    val mealRepo = MealRepository(db.mealDao())
    val userRepo = UserRepository(db.userDao())

    val mealViewModel: MealViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MealViewModel(mealRepo) as T
            }
        }
    )

    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(userRepo) as T
            }
        }
    )

    Box(Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "login") {

            composable("login") {
                LoginScreen(navController, userViewModel)
            }

            composable("signup") {
                SignupScreen(navController, userViewModel)
            }

            composable("home") {
                MainLayout(navController, userViewModel) {
                    HomeScreen(navController, mealViewModel, userViewModel)
                }
            }

            composable("reminder") {
                ReminderScreen(navController)
            }

            composable("history") {
                MainLayout(navController, userViewModel) {
                    HistoryScreen(mealViewModel)
                }
            }

            composable("profile") {
                MainLayout(navController, userViewModel) {
                    ProfileScreen(navController, userViewModel)
                }
            }

            composable("profile_summary") {
                MainLayout(navController, userViewModel) {
                    ProfileSummaryScreen(navController, userViewModel)
                }
            }

            composable("add") {
                AddMealScreen(navController, mealViewModel)
            }

            composable("search") {
                SearchScreen(navController, mealViewModel)
            }

            composable("users") {
                UserListScreen(navController, userViewModel)
            }

            composable("target") {
                MainLayout(navController, userViewModel) {
                    TargetScreen(navController)
                }
            }
        }

        if (showMealAlert) {
            MealReminderAlertDialog(onDismiss = { showMealAlert = false })
        }
    }
}
