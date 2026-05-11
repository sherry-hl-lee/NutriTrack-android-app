package com.example.ass2.viewmodel

import UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository
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
fun signup(email: String, password: String, onResult: (Boolean) -> Unit) {
    viewModelScope.launch {
        val success = repo.signup(email, password)

        if (success) {
            _isGuest.value = false
            _currentUser.value = User(email = email, password = password)
        }
        onResult(success)
    }
}
fun login(email: String, password: String, onResult: (LoginResult) -> Unit) {
    viewModelScope.launch {
        val result = repo.login(email, password)
        if (result is LoginResult.Success) {
            _isGuest.value = false
        }
        onResult(result)
    }
}
    fun loginAsGuest() {
        _isGuest.value = true
        _currentUser.value = null
    }

    fun logout() {
        _currentUser.value = null
        _isGuest.value = false
    }

    fun updateUserProfile(
        weight: Float,
        height: Float,
        age: Int,
        gender: String
    ) {
        viewModelScope.launch {

            // ✅ 更新本地状态（你原本的逻辑）
            _weight.value = weight
            _targetCalories.value = (weight * 30).toInt()

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
    sealed class LoginResult {
        object Success : LoginResult()
        object UserNotFound : LoginResult()
        object WrongPassword : LoginResult()
    }
}