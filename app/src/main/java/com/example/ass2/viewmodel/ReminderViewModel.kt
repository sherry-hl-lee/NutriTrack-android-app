package com.example.ass2.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.UserReminder
import com.example.ass2.data.repository.ReminderRepository
import com.example.ass2.reminder.ReminderScheduler
import com.example.ass2.reminder.ReminderType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val userEmail = MutableStateFlow<String?>(null)

    val reminders: StateFlow<List<UserReminder>> = userEmail
        .flatMapLatest { email ->
            if (email.isNullOrBlank()) flowOf(emptyList())
            else repository.observeByUser(email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setUserEmail(email: String?) {
        userEmail.value = email
    }

    fun addReminder(
        context: Context,
        type: ReminderType,
        hour: Int,
        minute: Int,
        onDone: (Boolean) -> Unit
    ) {
        val email = userEmail.value
        if (email.isNullOrBlank()) {
            onDone(false)
            return
        }
        viewModelScope.launch {
            val id = repository.insert(
                UserReminder(
                    userEmail = email,
                    type = type.id,
                    hour = hour,
                    minute = minute,
                    enabled = true
                )
            )
            val saved = repository.getById(id)
            if (saved != null) {
                ReminderScheduler.schedule(context.applicationContext, saved)
            }
            onDone(true)
        }
    }

    fun updateReminderTime(
        context: Context,
        reminder: UserReminder,
        hour: Int,
        minute: Int
    ) {
        viewModelScope.launch {
            val updated = reminder.copy(hour = hour, minute = minute)
            repository.update(updated)
            if (updated.enabled) {
                ReminderScheduler.schedule(context.applicationContext, updated)
            }
        }
    }

    fun setReminderEnabled(context: Context, reminder: UserReminder, enabled: Boolean) {
        viewModelScope.launch {
            val updated = reminder.copy(enabled = enabled)
            repository.update(updated)
            if (enabled) {
                ReminderScheduler.schedule(context.applicationContext, updated)
            } else {
                ReminderScheduler.cancel(context.applicationContext, updated.id)
            }
        }
    }

    fun deleteReminder(context: Context, reminder: UserReminder) {
        viewModelScope.launch {
            ReminderScheduler.cancel(context.applicationContext, reminder.id)
            repository.delete(reminder)
        }
    }

    /** Re-apply AlarmManager for all enabled reminders of the current user. */
    fun rescheduleForCurrentUser(context: Context) {
        val email = userEmail.value ?: return
        viewModelScope.launch {
            val list = repository.getEnabledByUser(email)
            ReminderScheduler.rescheduleAll(context.applicationContext, list)
        }
    }
}
