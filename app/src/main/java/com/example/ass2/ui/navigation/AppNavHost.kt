package com.example.ass2.ui.navigation

import UserRepository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.example.ass2.BuildConfig
import com.example.ass2.auth.GoogleAuthManager
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.repository.MealRepository
import com.example.ass2.data.repository.ReminderRepository
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
    val reminderRepo = ReminderRepository(db.userReminderDao())

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
    val aiSuggestViewModel: AiSuggestViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AiSuggestViewModel(BuildConfig.GEMINI_API_KEY) as T
            }
        }
    )

    val reminderViewModel: ReminderViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReminderViewModel(reminderRepo) as T
            }
        }
    )

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
        reminderViewModel.setUserEmail(email)
        aiSuggestViewModel.reset()
        if (email != null) {
            reminderViewModel.rescheduleForCurrentUser(context)
        }
    }

    val reminderPrefs = remember { ReminderPreferences(context) }
    var showMealAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("Reminder") }
    var alertBody by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current

    fun tryShowPendingDialog() {
        val email = currentUser?.email ?: return
        val pending = reminderPrefs.consumePendingMealAlert(email) ?: return
        alertTitle = pending.title
        alertBody = pending.body
        showMealAlert = true
    }

    DisposableEffect(lifecycleOwner, currentUser?.email) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                tryShowPendingDialog()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(currentUser?.email) {
        while (true) {
            delay(1000)
            val email = currentUser?.email
            if (email != null && reminderPrefs.hasPendingMealAlert(email)) {
                val pending = reminderPrefs.peekPendingAlert(email) ?: continue
                alertTitle = pending.title
                alertBody = pending.body
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
                ScreenSafeArea {
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
            }

            composable("signup") {
                ScreenSafeArea { SignupScreen(navController, userViewModel) }
            }

            composable("reset_password") {
                ScreenSafeArea { ResetPasswordScreen(navController, userViewModel) }
            }

            composable("home") {
                MainLayout(navController, userViewModel) {
                    HomeScreen(navController, mealViewModel, userViewModel)
                }
            }

            composable("reminder") {
                ScreenSafeArea {
                ReminderScreen(
                    navController = navController,
                    reminderViewModel = reminderViewModel,
                    userViewModel = userViewModel
                )
                }
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

                ScreenSafeArea {
                AddMealScreen(
                    navController = navController,
                    viewModel = mealViewModel,
                    mealId = mealId,
                    prefilledName = name,
                    prefilledCalories = calories,
                    prefilledMealType = mealType
                )
                }
            }

            composable ("search"){
                ScreenSafeArea {
                SearchScreen(navController, mealViewModel, userViewModel, foodViewModel)
                }
            }

            composable("ai_suggest") {
                ScreenSafeArea {
                AiSuggestScreen(
                    navController = navController,
                    mealViewModel = mealViewModel,
                    userViewModel = userViewModel,
                    aiSuggestViewModel = aiSuggestViewModel
                )
                }
            }

            composable("users") {
                ScreenSafeArea { UserListScreen(navController, userViewModel) }
            }

            composable("target") {
                MainLayout(navController, userViewModel){
                    TargetScreen(navController, targetViewModel)
                }
            }
            composable("insights") {
                ScreenSafeArea {
                InsightsScreen(navController, mealViewModel, targetViewModel)
                }
            }

            composable(
                route = "meals_day/{dayStart}",
                arguments = listOf(navArgument("dayStart") { type = NavType.LongType })
            ) { backStackEntry ->
                val dayStart = backStackEntry.arguments?.getLong("dayStart") ?: 0L
                ScreenSafeArea {
                MealDayDetailScreen(navController, mealViewModel, dayStart)
                }
            }
        }

        if (showMealAlert) {
            MealReminderAlertDialog(
                title = alertTitle,
                message = alertBody,
                onDismiss = {
                    showMealAlert = false
                    reminderPrefs.clearPendingAlert(currentUser?.email)
                }
            )
        }
    }
}

@Composable
private fun ScreenSafeArea(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        content()
    }
}