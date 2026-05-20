package com.example.ass2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.repository.FoodApiRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Food(val name: String, val calories: Int)

sealed class FoodSearchUiState {
    data object Idle : FoodSearchUiState()
    data object Loading : FoodSearchUiState()
    data class Success(val foods: List<Food>) : FoodSearchUiState()
    data class Error(val message: String) : FoodSearchUiState()
}

class FoodViewModel(
    private val foodApiRepository: FoodApiRepository = FoodApiRepository()
) : ViewModel() {

    private val _searchState = MutableStateFlow<FoodSearchUiState>(FoodSearchUiState.Idle)
    val searchState: StateFlow<FoodSearchUiState> = _searchState

    private var searchJob: Job? = null

    fun searchOnline(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchState.value = FoodSearchUiState.Idle
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            _searchState.value = FoodSearchUiState.Loading
            foodApiRepository.searchFoods(query)
                .onSuccess { foods ->
                    _searchState.value = FoodSearchUiState.Success(foods)
                }
                .onFailure { error ->
                    _searchState.value = FoodSearchUiState.Error(
                        error.message ?: "Failed to search USDA database"
                    )
                }
        }
    }

    fun clearOnlineSearch() {
        searchJob?.cancel()
        _searchState.value = FoodSearchUiState.Idle
    }
}
