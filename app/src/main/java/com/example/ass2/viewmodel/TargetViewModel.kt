package com.example.ass2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ass2.data.local.Target

class TargetViewModel : ViewModel() {

    var targets by mutableStateOf(
        listOf(
            Target(1, "Drink Water", 10),
            Target(2, "Eat Vegetables", 20),
            Target(3, "Exercise", 30)
        )
    )
        private set

    fun toggleTarget(target: Target) {
        targets = targets.map {
            if (it.id == target.id)
                it.copy(completed = !it.completed)
            else it
        }
    }

    val totalPoints: Int
        get() = targets.map { it.points }.sum()

    val earnedPoints: Int
        get() = targets
            .filter { it.completed }
            .map { it.points }
            .sum()
}