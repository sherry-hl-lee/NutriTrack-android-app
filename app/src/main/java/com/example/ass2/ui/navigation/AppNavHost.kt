package com.example.ass2.ui.navigation

import UserRepository
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.repository.MealRepository

import com.example.ass2.ui.screens.*
import com.example.ass2.viewmodel.*

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val context = LocalContext.current

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

        composable ("reminder"){
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

        composable ("search"){
            SearchScreen(navController, mealViewModel)
        }

        composable("users") {
            UserListScreen(navController,userViewModel)
        }

        composable("target") {
            MainLayout(navController, userViewModel){
                TargetScreen(navController)
            }
        }
    }
}