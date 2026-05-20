package com.example.ass2.viewmodel

import UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.User
import com.example.ass2.session.SessionPreferences
import com.example.ass2.util.ProfileHealthCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository,
    private val sessionPrefs: SessionPreferences
) : ViewModel() {
    // 👤 当前用户（null = 未登录）
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser
    // 👻 guest 模式
    private val _isGuest = MutableStateFlow(false)
    val isGuest: StateFlow<Boolean> = _isGuest
    // 🏋️ 用户信息
    private val _weight = MutableStateFlow(0f)
    val weight: StateFlow<Float> = _weight
    private val _targetCalories = MutableStateFlow(2000)
    val targetCalories: StateFlow<Int> = _targetCalories

    private val _sessionReady = MutableStateFlow(false)
    val sessionReady: StateFlow<Boolean> = _sessionReady

    private var googleSignOutHandler: (() -> Unit)? = null

    fun setGoogleSignOutHandler(handler: () -> Unit) {
        googleSignOutHandler = handler
    }

    init {
        viewModelScope.launch {
            restoreSession()
            _sessionReady.value = true
        }
    }

    private suspend fun restoreSession() {
        when {
            sessionPrefs.isGuestSession() -> {
                _isGuest.value = true
                _currentUser.value = null
            }
            else -> {
                val email = sessionPrefs.getSavedEmail() ?: return
                val user = repo.findUserByEmail(email)
                if (user != null) {
                    setLoggedInUser(user)
                } else {
                    sessionPrefs.clearSession()
                }
            }
        }
    }

fun signup(email: String, password: String, onResult: (Boolean) -> Unit) {
    viewModelScope.launch {
        val success = repo.signup(email, password)

        if (success) {
            _isGuest.value = false
            repo.findUserByEmail(email)?.let { setLoggedInUser(it) }
        }
        onResult(success)
    }
}
fun login(email: String, password: String, onResult: (LoginResult) -> Unit) {
    viewModelScope.launch {
        val result = repo.login(email, password)
        if (result is LoginResult.Success) {
            setLoggedInUser(result.user)
        }
        onResult(result)
    }
}

    fun loginWithGoogle(email: String, onResult: (LoginResult) -> Unit) {
        viewModelScope.launch {
            val user = repo.getOrCreateGoogleUser(email)
            setLoggedInUser(user)
            onResult(LoginResult.Success(user))
        }
    }

    fun loginAsGuest() {
        _isGuest.value = true
        _currentUser.value = null
        sessionPrefs.saveGuestSession()
    }

    fun logout() {
        _currentUser.value = null
        _isGuest.value = false
        _weight.value = 0f
        _targetCalories.value = 2000
        sessionPrefs.clearSession()
        googleSignOutHandler?.invoke()
    }

    private fun setLoggedInUser(user: User) {
        _isGuest.value = false
        _currentUser.value = user
        _weight.value = user.weight
        _targetCalories.value = ProfileHealthCalculator
            .calculate(user.weight, user.height, user.age, user.gender)
            ?.recommendedDailyCalories ?: 2000
        sessionPrefs.saveUserSession(user.email)
    }

    /** User has completed body profile in Room (used to open My Profile vs edit form). */
    fun hasSavedProfile(): Boolean {
        val u = _currentUser.value ?: return false
        return u.weight > 0f && u.height > 0f && u.age > 0
    }

    /** Reload logged-in user from Room so UI matches DB after tab switches. */
    suspend fun refreshCurrentUserFromDb() {
        val email = _currentUser.value?.email ?: return
        repo.findUserByEmail(email)?.let { setLoggedInUser(it) }
    }

    fun updateUserProfile(
        weight: Float,
        height: Float,
        age: Int,
        gender: String,
        onSaved: () -> Unit = {}
    ) {
        viewModelScope.launch {

            // ✅ 更新本地状态（你原本的逻辑）
            _weight.value = weight
            _targetCalories.value = ProfileHealthCalculator
                .calculate(weight, height, age, gender)
                ?.recommendedDailyCalories ?: 2000

            // ✅ 更新数据库用户（新增部分）
            val user = _currentUser.value ?: return@launch

            val updatedUser = user.copy(
                weight = weight,
                height = height,
                age = age,
                gender = gender
            )

            repo.updateUser(updatedUser)

            _currentUser.value = updatedUser
            onSaved()
        }
    }
    val users: StateFlow<List<User>> =
        repo.getAllUsers()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    fun deleteUser(user: User) {
        viewModelScope.launch {
            repo.deleteUser(user)
        }
    }

    fun verifyEmailExists(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(repo.findUserByEmail(email) != null)
        }
    }

    fun resetPassword(email: String, newPassword: String, onResult: (ResetPasswordResult) -> Unit) {
        viewModelScope.launch {
            val success = repo.resetPassword(email, newPassword)
            onResult(if (success) ResetPasswordResult.Success else ResetPasswordResult.UserNotFound)
        }
    }

    sealed class ResetPasswordResult {
        object Success : ResetPasswordResult()
        object UserNotFound : ResetPasswordResult()
    }

    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        object UserNotFound : LoginResult()
        object WrongPassword : LoginResult()
        object UseGoogleSignIn : LoginResult()
    }
}