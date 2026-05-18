package com.example.ass2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.DailyTargetLog
import com.example.ass2.data.local.Target
import com.example.ass2.data.repository.TargetRepository
import com.example.ass2.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TargetViewModel(
    private val repository: TargetRepository
) : ViewModel() {

    private val defaultTargets = listOf(
        Target(1, "Drink Water", 10, "💧", "Stay hydrated · 8 glasses"),
        Target(2, "Eat Vegetables", 20, "🥗", "Greens with lunch or dinner"),
        Target(3, "Exercise", 30, "🏃", "Move for at least 30 minutes")
    )

    var targets by mutableStateOf(defaultTargets.map { it.copy(completed = false) })
        private set

    private val userEmail = MutableStateFlow<String?>(null)

    val recentLogs: StateFlow<List<DailyTargetLog>> = userEmail
        .flatMapLatest { email ->
            if (email.isNullOrBlank()) flowOf(emptyList())
            else repository.observeRecent(email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPoints: Int
        get() = targets.sumOf { it.points }

    val earnedPoints: Int
        get() = targets.filter { it.completed }.sumOf { it.points }

    val isGoalAchieved: Boolean
        get() = totalPoints > 0 && earnedPoints >= totalPoints

    fun setUserEmail(email: String?) {
        userEmail.value = email
        if (email.isNullOrBlank()) {
            targets = defaultTargets.map { it.copy(completed = false) }
        } else {
            loadTodayProgress(email)
        }
    }

    fun toggleTarget(target: Target) {
        targets = targets.map {
            if (it.id == target.id) it.copy(completed = !it.completed) else it
        }
        persistTodayProgress()
    }

    private fun loadTodayProgress(email: String) {
        viewModelScope.launch {
            val dayStart = DateUtils.startOfDay(System.currentTimeMillis())
            val log = repository.getForDay(email, dayStart)
            targets = if (log != null) {
                val completedIds = log.completedTaskIds
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toSet()
                defaultTargets.map { it.copy(completed = completedIds.contains(it.id)) }
            } else {
                defaultTargets.map { it.copy(completed = false) }
            }
        }
    }

    private fun persistTodayProgress() {
        val email = userEmail.value ?: return
        viewModelScope.launch {
            val dayStart = DateUtils.startOfDay(System.currentTimeMillis())
            val completedIds = targets.filter { it.completed }.joinToString(",") { it.id.toString() }
            repository.upsert(
                DailyTargetLog(
                    userEmail = email,
                    dayStart = dayStart,
                    earnedPoints = earnedPoints,
                    totalPoints = totalPoints,
                    completedTaskIds = completedIds,
                    isGoalAchieved = isGoalAchieved
                )
            )
        }
    }
}
