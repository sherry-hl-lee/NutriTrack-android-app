package com.example.ass2.ui.navigation

import UserRepository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.repository.MealRepository
import com.example.ass2.data.repository.TargetRepository
import com.example.ass2.reminder.ReminderPreferences
import com.example.ass2.ui.components.MealReminderAlertDialog
import com.example.ass2.ui.screens.*
import kotlinx.coroutines.delay
import com.example.ass2.viewmodel.*

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val db = AppDatabase.getDatabase(context)
    val mealRepo = MealRepository(db.mealDao())
    val userRepo = UserRepository(db.userDao())
    val targetRepo = TargetRepository(db.dailyTargetDao())

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

    val targetViewModel: TargetViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TargetViewModel(targetRepo) as T
            }
        }
    )

    val currentUser by userViewModel.currentUser.collectAsState()
    LaunchedEffect(currentUser?.email) {
        val email = currentUser?.email
        mealViewModel.setLoggedInUserEmail(email)
        targetViewModel.setUserEmail(email)
    }

    val reminderPrefs = remember { ReminderPreferences(context) }
    var showMealAlert by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && reminderPrefs.consumePendingMealAlert()) {
                showMealAlert = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (reminderPrefs.hasPendingMealAlert()) {
                showMealAlert = true
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController, userViewModel)
        }

        composable("signup") {
            SignupScreen(navController, userViewModel)
        }

        composable("reset_password") {
            ResetPasswordScreen(navController, userViewModel)
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
                HistoryScreen(navController, mealViewModel)
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
            SearchScreen(navController, mealViewModel, userViewModel)
        }

        composable("users") {
            UserListScreen(navController,userViewModel)
        }

        composable("target") {
            MainLayout(navController, userViewModel){
                TargetScreen(navController, targetViewModel)
            }
        }
        composable("insights") {
            InsightsScreen(navController, mealViewModel, targetViewModel)
        }

        composable(
            route = "meals_day/{dayStart}",
            arguments = listOf(navArgument("dayStart") { type = NavType.LongType })
        ) { backStackEntry ->
            val dayStart = backStackEntry.arguments?.getLong("dayStart") ?: 0L
            MealDayDetailScreen(navController, mealViewModel, dayStart)
        }
    }

        if (showMealAlert) {
            MealReminderAlertDialog(
                onDismiss = {
                    showMealAlert = false
                    reminderPrefs.clearPendingMealAlert()
                }
            )
        }
    }
}