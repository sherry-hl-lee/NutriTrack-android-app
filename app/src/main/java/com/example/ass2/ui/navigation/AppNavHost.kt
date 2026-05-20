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
import com.example.ass2.auth.GoogleAuthManager
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.repository.MealRepository
import com.example.ass2.data.repository.TargetRepository
import com.example.ass2.reminder.ReminderPreferences
import com.example.ass2.session.SessionPreferences
import com.example.ass2.ui.components.MealReminderAlertDialog
import com.example.ass2.ui.screens.*
import kotlinx.coroutines.delay
import com.example.ass2.viewmodel.*

@Composable
fun AppNavHost(
    googleAuthManager: GoogleAuthManager
) {

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

    val sessionPrefs = remember { SessionPreferences(context) }

    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(userRepo, sessionPrefs) as T
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

    val foodViewModel: FoodViewModel = viewModel()

    val sessionReady by userViewModel.sessionReady.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    val isGuest by userViewModel.isGuest.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.setGoogleSignOutHandler { googleAuthManager.signOut() }
    }

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

    if (!sessionReady) {
        Box(Modifier.fillMaxSize())
        return
    }

    val startDestination = if (currentUser != null || isGuest) "home" else "login"

    Box(Modifier.fillMaxSize()) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel,
                onGoogleSignInClick = {
                    googleAuthManager.signIn { result ->
                        when (result) {
                            is GoogleAuthManager.GoogleSignInResult.Success -> {
                                userViewModel.loginWithGoogle(result.email) { loginResult ->
                                    if (loginResult is UserViewModel.LoginResult.Success) {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                            }
                            is GoogleAuthManager.GoogleSignInResult.Failure -> {
                                android.widget.Toast.makeText(
                                    context,
                                    result.message,
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                            GoogleAuthManager.GoogleSignInResult.Cancelled -> Unit
                        }
                    }
                }
            )
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

        composable(
            route = "add_meal?mealId={mealId}&name={name}&calories={calories}&mealType={mealType}",
            arguments = listOf(
                navArgument("mealId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("calories") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("mealType") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getInt("mealId") ?: -1
            val name = backStackEntry.arguments?.getString("name").orEmpty()
            val calories = backStackEntry.arguments?.getInt("calories") ?: -1
            val mealType = backStackEntry.arguments?.getString("mealType").orEmpty()

            AddMealScreen(
                navController = navController,
                viewModel = mealViewModel,
                mealId = mealId,
                prefilledName = name,
                prefilledCalories = calories,
                prefilledMealType = mealType
            )
        }

        composable ("search"){
            SearchScreen(navController, mealViewModel, userViewModel, foodViewModel)
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