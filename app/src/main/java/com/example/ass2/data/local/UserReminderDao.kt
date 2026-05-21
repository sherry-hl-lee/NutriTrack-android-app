package com.example.ass2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: UserReminder): Long

    @Update
    suspend fun update(reminder: UserReminder)

    @Delete
    suspend fun delete(reminder: UserReminder)

    @Query("SELECT * FROM user_reminders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserReminder?

    @Query("SELECT * FROM user_reminders WHERE userEmail = :email ORDER BY type, hour, minute")
    fun observeByUser(email: String): Flow<List<UserReminder>>

    @Query("SELECT * FROM user_reminders WHERE userEmail = :email AND enabled = 1")
    suspend fun getEnabledByUser(email: String): List<UserReminder>

    @Query("SELECT * FROM user_reminders WHERE enabled = 1")
    suspend fun getAllEnabled(): List<UserReminder>
}
