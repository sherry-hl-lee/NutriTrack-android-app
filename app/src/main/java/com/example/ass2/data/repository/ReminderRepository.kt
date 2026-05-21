package com.example.ass2.data.repository

import com.example.ass2.data.local.UserReminder
import com.example.ass2.data.local.UserReminderDao
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: UserReminderDao) {

    fun observeByUser(userEmail: String): Flow<List<UserReminder>> =
        dao.observeByUser(userEmail)

    suspend fun getById(id: Long): UserReminder? = dao.getById(id)

    suspend fun getEnabledByUser(userEmail: String): List<UserReminder> =
        dao.getEnabledByUser(userEmail)

    suspend fun getAllEnabled(): List<UserReminder> = dao.getAllEnabled()

    suspend fun insert(reminder: UserReminder): Long = dao.insert(reminder)

    suspend fun update(reminder: UserReminder) = dao.update(reminder)

    suspend fun delete(reminder: UserReminder) = dao.delete(reminder)
}
