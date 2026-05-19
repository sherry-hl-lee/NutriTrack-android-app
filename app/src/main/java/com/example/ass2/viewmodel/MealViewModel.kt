package com.example.ass2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Meal
import com.example.ass2.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    private val userEmail = MutableStateFlow<String?>(null)

    val meals: StateFlow<List<Meal>> = userEmail
        .flatMapLatest { email ->
            if (email.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                repository.getMealsByUserEmail(email)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setLoggedInUserEmail(email: String?) {
        userEmail.value = email
    }

    fun addMeal(
        name: String,
        calories: Int,
        mealType: String,
        date: Long
    ) {
        val email = userEmail.value ?: return
        viewModelScope.launch {
            repository.insertMeal(
                Meal(
                    userEmail = email,
                    name = name,
                    calories = calories,
                    mealType = mealType,
                    date = date
                )
            )
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    suspend fun getMealById(id: Int): Meal? {
        return repository.getMealById(id)
    }
    fun updateMeal(meal: Meal) {
        val email = userEmail.value ?: return
        if (meal.userEmail != email) return
        viewModelScope.launch {
            repository.updateMeal(meal)
        }
    }

}
